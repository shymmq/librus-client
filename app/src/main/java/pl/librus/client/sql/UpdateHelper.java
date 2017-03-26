package pl.librus.client.sql;

import android.content.Context;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.immutables.value.Value;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.requery.Persistable;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import pl.librus.client.LibrusUtils;
import pl.librus.client.api.APIClient;
import pl.librus.client.api.DatabaseStrategy;
import pl.librus.client.api.IAPIClient;
import pl.librus.client.api.ProgressReporter;
import pl.librus.client.datamodel.Average;
import pl.librus.client.datamodel.Event;
import pl.librus.client.datamodel.EventCategory;
import pl.librus.client.datamodel.Identifiable;
import pl.librus.client.datamodel.LibrusColor;
import pl.librus.client.datamodel.LuckyNumber;
import pl.librus.client.datamodel.Me;
import pl.librus.client.datamodel.PlainLesson;
import pl.librus.client.datamodel.Teacher;
import pl.librus.client.datamodel.announcement.Announcement;
import pl.librus.client.datamodel.attendance.Attendance;
import pl.librus.client.datamodel.attendance.AttendanceCategory;
import pl.librus.client.datamodel.grade.Grade;
import pl.librus.client.datamodel.grade.GradeCategory;
import pl.librus.client.datamodel.grade.GradeComment;
import pl.librus.client.datamodel.subject.Subject;

import static pl.librus.client.sql.EntityChange.Type.ADDED;
import static pl.librus.client.sql.EntityChange.Type.CHANGED;

/**
 * Created by szyme on 31.01.2017.
 * Contains methods to update data from server
 */

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
    private final DatabaseStrategy databaseStrategy;
    private final IAPIClient serverStrategy;

    public UpdateHelper(Context context) {
        this(DatabaseStrategy.getInstance(context), new APIClient(context));
    }

    public UpdateHelper(DatabaseStrategy databaseStrategy, IAPIClient serverStrategy) {
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

    public <T extends Identifiable> Observable<EntityChange<T>> reload(Class<T> clazz) {
        return Single.zip(
                databaseStrategy.getAll(clazz).toList(),
                serverStrategy.getAll(clazz).toList(),
                ImmutableListTuple::of)
                .doOnSuccess(tuple -> databaseStrategy.upsert(tuple.fromServer()))
                .flattenAsObservable(this::detectChanges);
    }

    public Observable<EntityChange<? extends Identifiable>> reloadMany(Class<? extends Identifiable>... classes) {
        return Observable.fromArray(classes)
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
