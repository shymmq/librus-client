package pl.librus.client.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.immutables.value.Value;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.requery.Persistable;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import pl.librus.client.UserScope;
import pl.librus.client.data.db.DatabaseManager;
import pl.librus.client.data.server.APIClient;
import pl.librus.client.data.server.IAPIClient;
import pl.librus.client.domain.Average;
import pl.librus.client.domain.Event;
import pl.librus.client.domain.EventCategory;
import pl.librus.client.domain.Identifiable;
import pl.librus.client.domain.LibrusColor;
import pl.librus.client.domain.LuckyNumber;
import pl.librus.client.domain.Me;
import pl.librus.client.domain.PlainLesson;
import pl.librus.client.domain.Teacher;
import pl.librus.client.domain.announcement.Announcement;
import pl.librus.client.domain.attendance.Attendance;
import pl.librus.client.domain.attendance.AttendanceCategory;
import pl.librus.client.domain.grade.Grade;
import pl.librus.client.domain.grade.GradeCategory;
import pl.librus.client.domain.grade.GradeComment;
import pl.librus.client.domain.lesson.Lesson;
import pl.librus.client.domain.subject.Subject;
import pl.librus.client.ui.ProgressReporter;
import pl.librus.client.util.LibrusUtils;

import static pl.librus.client.data.EntityChange.Type.ADDED;
import static pl.librus.client.data.EntityChange.Type.CHANGED;

/**
 * Created by szyme on 31.01.2017.
 * Contains methods to update data from server
 */

@UserScope
public class UpdateHelper {
    @SuppressWarnings("unchecked")
    private static final List<Class<? extends Persistable>> entitiesToUpdate = Lists.newArrayList(
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
    private final DatabaseManager databaseStrategy;
    private final IAPIClient serverStrategy;

    @Inject
    public UpdateHelper(DatabaseManager databaseStrategy, IAPIClient serverStrategy) {
        this.databaseStrategy = databaseStrategy;
        this.serverStrategy = serverStrategy;
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
        LocalDate lastMonday = LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY);
        List<LocalDate> weekStarts = Lists.newArrayList(lastMonday, lastMonday.plusWeeks(1));

        return StreamSupport.stream(weekStarts)
                .map(ws -> serverStrategy.getLessonsForWeek(ws)
                        .toList()
                        .doOnSuccess(databaseStrategy::upsert)
                        .doOnSuccess(list -> LibrusUtils.log("Loaded timetable for %s", ws)))
                .collect(Collectors.toList());
    }

    private <T extends Persistable> Single<?> update(Class<T> clazz) {
        return serverStrategy.getAll(clazz)
                .toList()
                .doOnSuccess(databaseStrategy::upsert)
                .doOnSuccess(list -> LibrusUtils.log("Loaded %s %s", list.size(), clazz.getSimpleName()));
    }

    public Observable<EntityChange<Lesson>> reloadLessons(List<LocalDate> weekStarts) {
        return Single.zip(
                Observable.fromIterable(weekStarts)
                        .flatMap(databaseStrategy::getLessonsForWeek)
                        .toList(),
                Observable.fromIterable(weekStarts)
                        .flatMap(serverStrategy::getLessonsForWeek)
                        .toList(),
                ImmutableListTuple::of)
                .doOnSuccess(tuple -> {
                    databaseStrategy.clearAll(Lesson.class);
                    databaseStrategy.upsert(tuple.fromServer());
                })
                .flattenAsObservable(this::detectChanges);
    }

    public <T extends Identifiable> Observable<EntityChange<T>> reload(Class<T> clazz) {
        return Single.zip(
                databaseStrategy.getAll(clazz).toList(),
                serverStrategy.getAll(clazz).toList(),
                ImmutableListTuple::of)
                .doOnSuccess(tuple -> databaseStrategy.upsert(tuple.fromServer()))
                .flattenAsObservable(this::detectChanges);
    }

    public Observable<EntityChange<? extends Identifiable>> reloadMany(List<Class<? extends Identifiable>> classes) {
        return Observable.fromIterable(classes)
                .flatMap(this::reload);
    }

    private <T extends Identifiable> List<EntityChange<T>> detectChanges(ListTuple<T> listTuple) {
        Map<String, T> byId = Maps.uniqueIndex(listTuple.fromDb(), t -> t.id());

        ImmutableList.Builder builder = ImmutableList.<EntityChange<T>>builder();
        for (T newEntity : listTuple.fromServer()) {
            T inDB = byId.get(newEntity.id());
            if (inDB == null) {
                builder.add(ImmutableEntityChange.of(ADDED, newEntity));
            } else if (!inDB.equals(newEntity)) {
                builder.add(ImmutableEntityChange.of(CHANGED, newEntity));
            }
        }
        return builder.build();
    }

    @Value.Immutable
    interface ListTuple<T> {
        @Value.Parameter
        List<T> fromDb();

        @Value.Parameter
        List<T> fromServer();
    }

}
