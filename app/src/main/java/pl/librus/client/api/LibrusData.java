package pl.librus.client.api;

import android.content.Context;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.joda.time.LocalDate;

import java.util.List;
import java.util.Map;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveSupport;
import io.requery.sql.TableCreationMode;
import pl.librus.client.BuildConfig;
import pl.librus.client.datamodel.Announcement;
import pl.librus.client.datamodel.Attendance;
import pl.librus.client.datamodel.AttendanceCategory;
import pl.librus.client.datamodel.AttendanceWithCategory;
import pl.librus.client.datamodel.BaseAttendance;
import pl.librus.client.datamodel.FullAnnouncement;
import pl.librus.client.datamodel.FullAttendance;
import pl.librus.client.datamodel.ImmutableAttendanceWithCategory;
import pl.librus.client.datamodel.ImmutableFullAnnouncement;
import pl.librus.client.datamodel.ImmutableFullAttendance;
import pl.librus.client.datamodel.Lesson;
import pl.librus.client.datamodel.LessonType;
import pl.librus.client.datamodel.LuckyNumber;
import pl.librus.client.datamodel.LuckyNumberType;
import pl.librus.client.datamodel.Me;
import pl.librus.client.datamodel.Models;
import pl.librus.client.datamodel.PlainLesson;
import pl.librus.client.datamodel.Subject;
import pl.librus.client.datamodel.Teacher;
import pl.librus.client.datamodel.Timetable;
import pl.librus.client.sql.SqlHelper;

public class LibrusData {
    private static ReactiveEntityStore<Persistable> dataStore;

    private static IAPIClient apiClient;

    public static ReactiveEntityStore<Persistable> init(Context context, IAPIClient apiClient, String login) {
        LibrusData.apiClient = apiClient;
        if (dataStore == null) {
            DatabaseSource source = new DatabaseSource(context, Models.DEFAULT, databaseName(login), 12);
            if (BuildConfig.DEBUG) {
                source.setLoggingEnabled(true);
                source.setTableCreationMode(TableCreationMode.DROP_CREATE);
            }
            dataStore = ReactiveSupport.toReactiveStore(SqlHelper.getDataStore(source));
        }
        return dataStore;
    }

    public static void delete(Context context, String login) {
        context.deleteDatabase(databaseName(login));
        close();
    }

    public static void close() {
        if (dataStore != null) {
            dataStore.close();
            dataStore = null;
        }
    }

    private static String databaseName(String login) {
        return "user-data-" + login;
    }

    public static <T extends Persistable> Single<T> findByKey(Class<T> clazz, String key) {
        if (key == null) {
            return null;
        }
        return findByKeyInDb(clazz, key)
                .switchIfEmpty(updateFromServerById(clazz, key).toMaybe())
                .toSingle();
    }

    public static <T extends Persistable> Maybe<T> findByKeyInDb(Class<T> clazz, String key) {
        return dataStore.findByKey(clazz, key);
    }

    public static <T extends Persistable> Single<T> updateFromServerById(Class<T> clazz, String key) {
        return apiClient.getById(clazz, key)
                .flatMap(dataStore::upsert);
    }

    public static <T extends Persistable> Single<List<T>> findAll(Class<T> clazz) {
        Single<List<T>> inDB = findAllInDb(clazz);

        Single<List<T>> onServer = updateAllFromServer(clazz);

        return inDB.flatMap(cached -> cached.isEmpty() ? onServer : Single.just(cached));
    }

    public static <T extends Persistable> Single<List<T>> findAllInDb(Class<T> clazz) {
        return dataStore.select(clazz)
                .get()
                .observable()
                .toList();
    }

    public static <T extends Persistable> Single<List<T>> updateAllFromServer(Class<T> clazz) {
        return apiClient.getAll(clazz)
                .flatMap(LibrusData::upsert);
    }

    private static <T extends Persistable> Single<List<T>> upsert(List<T> list) {
        return dataStore.upsert(list)
                .map(Lists::newArrayList);
    }

    public static Single<List<Lesson>> findLessonsForWeek(LocalDate weekStart) {
        Single<List<Lesson>> inDB = findLessonsForWeekInDb(weekStart);

        Single<List<Lesson>> onServer = updateTimetableFromServer(weekStart);

        return inDB.flatMap(cached -> cached.isEmpty() ? onServer : Single.just(cached));
    }

    public static Single<List<Lesson>> findLessonsForWeekInDb(LocalDate weekStart) {
        return dataStore
                .select(Lesson.class)
                .where(LessonType.DATE.gte(weekStart))
                .and(LessonType.DATE.lt(weekStart.plusWeeks(1)))
                .get()
                .observable()
                .toList();
    }

    public static Single<List<Lesson>> updateTimetableFromServer(LocalDate weekStart) {
        return apiClient.getTimetable(weekStart)
                .map(Timetable::toLessons)
                .flatMap(LibrusData::upsert);
    }

    public static Single<Me> findMe() {
        return dataStore.select(Me.class)
                .get()
                .observable()
                .toList()
                .map(Iterables::getOnlyElement)
                .subscribeOn(Schedulers.io());
    }

    public static Single<Optional<LuckyNumber>> findLuckyNumber() {
        return dataStore.select(LuckyNumber.class)
                .orderBy(LuckyNumberType.DAY.desc())
                .limit(1)
                .get()
                .observable()
                .map(Optional::of)
                .first(Optional.absent())
                .subscribeOn(Schedulers.io());
    }

    public static Observable<? extends FullAttendance> findFullAttendance(BaseAttendance attendance) {
        return Single.zip(
                findByKey(Teacher.class, attendance.addedById()),
                findByKey(AttendanceCategory.class, attendance.categoryId()),
                findByKey(PlainLesson.class, attendance.lessonId())
                        .flatMap(lesson -> findByKey(Subject.class, lesson.subject())),
                (t, ac, s) -> ImmutableFullAttendance.builder()
                        .from(attendance)
                        .addedBy(t)
                        .category(ac)
                        .subject(s)
                        .build())
                .toObservable();
    }

    private static Single<? extends List<? extends AttendanceWithCategory>> mapAttendancesToCategories(
            Map<String, AttendanceCategory> categoryMap) {
        return findAll(Attendance.class)
                .flattenAsObservable(l -> l)
                .map(attendance -> ImmutableAttendanceWithCategory.builder()
                        .from(attendance)
                        .category(categoryMap.get(attendance.categoryId()))
                        .build())
                .toList();
    }

    public static Single<? extends List<? extends AttendanceWithCategory>> findAttendancesWithCategories() {
        return findAll(AttendanceCategory.class)
                .map(list -> Maps.uniqueIndex(list, AttendanceCategory::id))
                .flatMap(LibrusData::mapAttendancesToCategories);
    }

    public static Single<? extends List<? extends FullAnnouncement>> findFullAnnouncements() {
        return findAll(Announcement.class)
                .flattenAsObservable(l -> l)
                .flatMap(announcement -> findByKey(Teacher.class, announcement.addedById())
                        .map(teacher -> ImmutableFullAnnouncement.builder()
                                .from(announcement)
                                .addedBy(teacher)
                                .build())
                        .toObservable())
                .toList();
    }

    public static ReactiveEntityStore<Persistable> getDataStore() {
        return dataStore;
    }
}
