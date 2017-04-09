package pl.librus.client.data;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.immutables.value.Value;
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import pl.librus.client.UserScope;
import pl.librus.client.data.db.DatabaseManager;
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
    private static final List<Class<? extends Identifiable>> entitiesToUpdate = Lists.newArrayList(
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

    public Observable<EntityChange<?>> tryReloadAll() {
        LibrusUtils.log("Trying to reload all");
        ImmutableList.Builder<Observable<EntityChange<?>>> builder = ImmutableList.builder();

        for (Class<? extends Identifiable> entityClass : entitiesToUpdate) {
            builder.add(shouldUpdate(entityClass)
                    .flatMapObservable(should -> reload(entityClass)));
        }
        builder.add(shouldUpdate(Lesson.class)
                .flatMapObservable(should -> reloadLessons()));

        return Observable.merge(builder.build());
    }

    public Completable updateAll(ProgressReporter progressReporter) {
        ImmutableList.Builder<Completable> builder = ImmutableList.builder();

        for (Class<? extends Persistable> entityClass : entitiesToUpdate) {
            builder.add(update(entityClass, progressReporter));
        }
        builder.addAll(updateNearestTimetables(progressReporter));
        List<Completable> tasks = builder.build();
        progressReporter.setTotal(tasks.size());
        return Completable.merge(tasks);
    }

    private List<Completable> updateNearestTimetables(ProgressReporter progressReporter) {
        return StreamSupport.stream(weekStarts())
                .map(ws -> serverStrategy.getLessonsForWeek(ws)
                        .toList()
                        .doOnSuccess(list -> LibrusUtils.log("Loaded timetable for %s", ws))
                        .doOnSuccess(progressReporter::report)
                        .flatMapCompletable(databaseStrategy::upsert))
                .collect(Collectors.toList());
    }

    @NonNull
    private List<LocalDate> weekStarts() {
        LocalDate lastMonday = LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY);
        return Lists.newArrayList(lastMonday, lastMonday.plusWeeks(1));
    }

    private <T extends Persistable> Completable update(Class<T> clazz, ProgressReporter progressReporter) {
        return serverStrategy.getAll(clazz)
                .toList()
                .doOnSuccess(list -> LibrusUtils.log("Loaded %s %s", list.size(), clazz.getSimpleName()))
                .doOnSuccess(progressReporter::report)
                .flatMapCompletable(databaseStrategy::upsert);
    }

    public Observable<EntityChange<Lesson>> reloadLessons() {
        LibrusUtils.log("Reloading lessons");

        return Single.zip(
                Observable.fromIterable(weekStarts())
                        .flatMap(databaseStrategy::getLessonsForWeek)
                        .toList(),
                Observable.fromIterable(weekStarts())
                        .flatMap(serverStrategy::getLessonsForWeek)
                        .toList(),
                ImmutableListTuple::of)
                .flatMap(tuple -> databaseStrategy.clearAll(Lesson.class)
                        .andThen(databaseStrategy.upsert(tuple.fromServer()))
                        .toSingleDefault(tuple))
                .observeOn(Schedulers.computation())
                .flattenAsObservable(this::detectChanges);
    }

    public <T extends Identifiable> Observable<EntityChange<T>> reload(Class<T> clazz) {
        LibrusUtils.log("Reloading %s", clazz.getSimpleName());
        return Single.zip(
                databaseStrategy.getAll(clazz).toList(),
                serverStrategy.getAll(clazz).toList(),
                ImmutableListTuple::of)
                .flatMap(tuple -> databaseStrategy.upsert(tuple.fromServer())
                        .toSingleDefault(tuple))
                .observeOn(Schedulers.computation())
                .flattenAsObservable(this::detectChanges);
    }

    public Observable<EntityChange<? extends Identifiable>> reloadMany(Collection<Class<? extends Identifiable>> classes) {
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
                LibrusUtils.log("New %s", newEntity);
            } else if (!inDB.equals(newEntity)) {
                builder.add(ImmutableEntityChange.of(CHANGED, newEntity));
                LibrusUtils.log("Changed: \n%s -> \n%s", inDB, newEntity);
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

    @VisibleForTesting
    public Maybe<?> shouldUpdate(Class<? extends Persistable> clazz) {
        return databaseStrategy.findLastUpdate(clazz)
                .observeOn(Schedulers.computation())
                .map(LastUpdate::date)
                .map(date -> Days.daysBetween(date, LocalDate.now()).getDays())
                .map(diff -> diff > EntityInfos.infoFor(clazz).refreshDays())
                .toSingle(true)
                .filter(should -> should);
    }

}
