package pl.librus.client.sql;

import android.content.Context;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.requery.Persistable;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import pl.librus.client.api.APIClient;
import pl.librus.client.api.IAPIClient;
import pl.librus.client.api.ProgressReporter;
import pl.librus.client.datamodel.Announcement;
import pl.librus.client.datamodel.Attendance;
import pl.librus.client.datamodel.AttendanceCategory;
import pl.librus.client.datamodel.Average;
import pl.librus.client.datamodel.Event;
import pl.librus.client.datamodel.EventCategory;
import pl.librus.client.datamodel.Grade;
import pl.librus.client.datamodel.GradeCategory;
import pl.librus.client.datamodel.GradeComment;
import pl.librus.client.datamodel.Identifiable;
import pl.librus.client.datamodel.Lesson;
import pl.librus.client.datamodel.LessonType;
import pl.librus.client.datamodel.LibrusColor;
import pl.librus.client.datamodel.LuckyNumber;
import pl.librus.client.datamodel.Me;
import pl.librus.client.datamodel.PlainLesson;
import pl.librus.client.datamodel.Subject;
import pl.librus.client.datamodel.Teacher;
import pl.librus.client.datamodel.Timetable;
import pl.librus.client.timetable.TimetableUtils;
import pl.librus.client.ui.MainApplication;

import static pl.librus.client.sql.EntityChange.Type.ADDED;
import static pl.librus.client.sql.EntityChange.Type.CHANGED;

/**
 * Created by szyme on 31.01.2017.
 * Contains methods to update data from server
 */

public class UpdateHelper {
    private final IAPIClient client;
    @SuppressWarnings("unchecked")
    private final List<Class<? extends Persistable>> entitiesToUpdate = Lists.newArrayList(
            Announcement.class,
            Subject.class,
            Teacher.class,
            Grade.class,
            GradeCategory.class,
            GradeComment.class,
            PlainLesson.class,
            Event.class,
            EventCategory.class,
            Attendance.class,
            AttendanceCategory.class,
            Average.class,
            LibrusColor.class,
            LuckyNumber.class,
            Me.class
    );

    public UpdateHelper(Context context) {
        this(new APIClient(context));
    }

    public UpdateHelper(IAPIClient client) {
        this.client = client;
    }

    public Flowable<Object> updateAll(ProgressReporter progressReporter) {
        ImmutableList.Builder<Single<?>> builder = ImmutableList.builder();

        for (Class<? extends Persistable> entityClass : entitiesToUpdate) {
            builder.add(update(entityClass));
        }
        builder.addAll(updateNearestTimetables());
        List<Single<?>> tasks = builder.build();
        progressReporter.setTotal(tasks.size());
        return Single.merge(tasks);
    }

    private List<Single<?>> updateNearestTimetables() {
        List<LocalDate> weekStarts = TimetableUtils.getNextFullWeekStarts(LocalDate.now());
        return StreamSupport.stream(weekStarts)
                .map(this::updateTimetable)
                .collect(Collectors.toList());
    }

    private Single<List<Lesson>> updateTimetable(LocalDate weekStart) {
        return client.getTimetable(weekStart)
                .map(Timetable::toLessons)
                .flatMap(MainApplication.getData()::upsert)
                .map(Lists::newArrayList);
    }

    private <T extends Persistable> Single<List<T>> update(final Class<T> clazz) {
        return client.getAll(clazz)
                .flatMap(MainApplication.getData()::upsert)
                .map(Lists::newArrayList);
    }

    public Single<List<Lesson>> getLessonsForWeek(LocalDate weekStart) {
        return MainApplication.getData()
                .select(Lesson.class)
                .where(LessonType.DATE.gte(weekStart))
                .and(LessonType.DATE.lt(weekStart.plusWeeks(1)))
                .get()
                .observable()
                .toList()
                .flatMap(cached -> cached.isEmpty() ? updateTimetable(weekStart) : Single.just(cached));
    }

    public <T extends Persistable & Identifiable, E> Single<List<EntityChange<T>>> reload(Class<T> clazz) {
        Single<Map<String, T>> fromDB = MainApplication.getData()
                .select(clazz)
                .get()
                .observable()
                .toMap(t -> t.id());

        Single<List<T>> fromServer = client.getAll(clazz);

        return Single.zip(fromDB, fromServer, this::detectChanges);
    }

    private <T extends Persistable & Identifiable> List<EntityChange<T>> detectChanges(Map<String, T> byId, List<T> fromServer) {
        MainApplication.getData().upsert(fromServer).blockingGet();
        ImmutableList.Builder builder = ImmutableList.<EntityChange<T>>builder();
        for (T newEntity : fromServer) {
            T inDB = byId.get(newEntity.id());
            if (inDB == null) {
                builder.add(ImmutableEntityChange.of(ADDED, newEntity));
            } else if (!inDB.equals(newEntity)) {
                builder.add(ImmutableEntityChange.of(CHANGED, newEntity));
            }
        }
        return builder.build();
    }

}
