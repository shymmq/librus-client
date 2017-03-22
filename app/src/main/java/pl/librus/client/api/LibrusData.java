package pl.librus.client.api;

import android.content.Context;
import android.preference.PreferenceManager;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.joda.time.LocalDate;

import java.util.List;
import java.util.Map;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveSupport;
import io.requery.sql.TableCreationMode;
import pl.librus.client.BuildConfig;
import pl.librus.client.datamodel.Average;
import pl.librus.client.datamodel.Identifiable;
import pl.librus.client.datamodel.LibrusColor;
import pl.librus.client.datamodel.LuckyNumber;
import pl.librus.client.datamodel.LuckyNumberType;
import pl.librus.client.datamodel.Me;
import pl.librus.client.datamodel.Models;
import pl.librus.client.datamodel.PlainLesson;
import pl.librus.client.datamodel.Teacher;
import pl.librus.client.datamodel.announcement.Announcement;
import pl.librus.client.datamodel.announcement.ImmutableFullAnnouncement;
import pl.librus.client.datamodel.attendance.Attendance;
import pl.librus.client.datamodel.attendance.AttendanceCategory;
import pl.librus.client.datamodel.attendance.BaseAttendance;
import pl.librus.client.datamodel.attendance.ImmutableEnrichedAttendance;
import pl.librus.client.datamodel.attendance.ImmutableFullAttendance;
import pl.librus.client.datamodel.grade.EnrichedGrade;
import pl.librus.client.datamodel.grade.Grade;
import pl.librus.client.datamodel.grade.GradeCategory;
import pl.librus.client.datamodel.grade.GradeComment;
import pl.librus.client.datamodel.grade.ImmutableEnrichedGrade;
import pl.librus.client.datamodel.grade.ImmutableFullGrade;
import pl.librus.client.datamodel.grade.ImmutableFullGradeCategory;
import pl.librus.client.datamodel.lesson.Lesson;
import pl.librus.client.datamodel.lesson.LessonType;
import pl.librus.client.datamodel.lesson.Timetable;
import pl.librus.client.datamodel.subject.ImmutableFullSubject;
import pl.librus.client.datamodel.subject.Subject;
import pl.librus.client.sql.SqlHelper;

public class LibrusData {
    private final ReactiveEntityStore<Persistable> dataStore;

    private final IAPIClient apiClient;

    private static LibrusData instance;

    private LibrusData(ReactiveEntityStore<Persistable> dataStore, IAPIClient apiClient) {
        this.dataStore = dataStore;
        this.apiClient = apiClient;
    }

    public static LibrusData getInstance(Context context, IAPIClient apiClient, String login) {
        if (instance != null) return instance;
        DatabaseSource source = new DatabaseSource(context, Models.DEFAULT, databaseName(login), 15);
        if (BuildConfig.DEBUG) {
            source.setLoggingEnabled(true);
            source.setTableCreationMode(TableCreationMode.DROP_CREATE);
        }
        ReactiveEntityStore<Persistable> dataStore = ReactiveSupport.toReactiveStore(SqlHelper.getDataStore(source));
        instance = new LibrusData(dataStore, apiClient);
        return instance;
    }

    public static LibrusData getInstance(Context context) {
        String login = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("login", null);
        login = Preconditions.checkNotNull(login);
        IAPIClient client = new APIClient(context);
        return getInstance(context, client, login);
    }

    public static void delete(Context context, String login) {
        context.deleteDatabase(databaseName(login));
        close();
    }

    public static void close() {
        if (instance != null) {
            instance.dataStore.close();
            instance = null;
        }
    }

    private static String databaseName(String login) {
        return "user-data-" + login;
    }

    public <T extends Persistable & Identifiable> Single<Optional<T>> findByKey(Class<T> clazz, Optional<String> key) {
        if(key.isPresent()) {
            return findByKey(clazz, key.get())
                    .map(Optional::of);
        } else {
            return Single.just(Optional.absent());
        }
    }

    public <T extends Persistable & Identifiable> Single<T> findByKey(Class<T> clazz, String key) {
        if (key == null) {
            return null;
        }
        return findByKeyInDb(clazz, key)
                .switchIfEmpty(updateFromServerById(clazz, key).toMaybe())
                .toSingle();
    }

    public <T extends Persistable> Maybe<T> findByKeyInDb(Class<T> clazz, String key) {
        return dataStore.findByKey(clazz, key);
    }

    public <T extends Persistable & Identifiable> Single<T> updateFromServerById(Class<T> clazz, String key) {
        return apiClient.getById(clazz, key)
                .flatMap(dataStore::upsert);
    }

    public <T extends Persistable> Single<List<T>> findAll(Class<T> clazz) {
        Single<List<T>> inDB = findAllInDb(clazz);

        Single<List<T>> onServer = updateAllFromServer(clazz);

        return inDB.flatMap(cached -> cached.isEmpty() ? onServer : Single.just(cached));
    }

    public <T extends Persistable> Single<List<T>> findAllInDb(Class<T> clazz) {
        return dataStore.select(clazz)
                .get()
                .observable()
                .toList();
    }

    public <T extends Persistable> Single<List<T>> updateAllFromServer(Class<T> clazz) {
        return apiClient.getAll(clazz)
                .flatMap(this::upsert);
    }

    private <T extends Persistable> Single<List<T>> upsert(List<T> list) {
        return dataStore.upsert(list)
                .map(Lists::newArrayList);
    }

    public Single<List<Lesson>> findLessonsForWeek(LocalDate weekStart) {
        Single<List<Lesson>> inDB = findLessonsForWeekInDb(weekStart);

        Single<List<Lesson>> onServer = updateTimetableFromServer(weekStart);

        return inDB.flatMap(cached -> cached.isEmpty() ? onServer : Single.just(cached));
    }

    public Single<List<Lesson>> findLessonsForWeekInDb(LocalDate weekStart) {
        return dataStore
                .select(Lesson.class)
                .where(LessonType.DATE.gte(weekStart))
                .and(LessonType.DATE.lt(weekStart.plusWeeks(1)))
                .get()
                .observable()
                .toList();
    }

    public Single<List<Lesson>> updateTimetableFromServer(LocalDate weekStart) {
        return apiClient.getTimetable(weekStart)
                .map(Timetable::toLessons)
                .flatMap(this::upsert);
    }

    public Single<Me> findMe() {
        return dataStore.select(Me.class)
                .get()
                .observable()
                .toList()
                .map(Iterables::getOnlyElement)
                .subscribeOn(Schedulers.io());
    }

    public Single<Optional<LuckyNumber>> findLuckyNumber() {
        return dataStore.select(LuckyNumber.class)
                .orderBy(LuckyNumberType.DAY.desc())
                .limit(1)
                .get()
                .observable()
                .map(Optional::of)
                .first(Optional.absent())
                .subscribeOn(Schedulers.io());
    }

    public Observable<ImmutableFullAttendance> findFullAttendance(BaseAttendance attendance) {
        return Single.zip(
                findByKey(Teacher.class, attendance.addedById()),
                findByKey(AttendanceCategory.class, attendance.categoryId()),
                findByKey(PlainLesson.class, attendance.lessonId())
                        .flatMap(lesson -> findByKey(Subject.class, lesson.transform(PlainLesson::subject))),
                (t, ac, s) -> ImmutableFullAttendance.builder()
                        .from(attendance)
                        .addedBy(t)
                        .category(ac)
                        .subject(s)
                        .build())
                .toObservable();
    }

    private Single<List<ImmutableEnrichedAttendance>> makeEnrichedAttendances(
            Map<String, AttendanceCategory> categoryMap) {
        return findAll(Attendance.class)
                .flattenAsObservable(l -> l)
                .map(attendance -> ImmutableEnrichedAttendance.builder()
                        .from(attendance)
                        .category(categoryMap.get(attendance.categoryId()))
                        .build())
                .toList();
    }

    public Single<List<ImmutableEnrichedAttendance>> findEnrichedAttendances() {
        return findAll(AttendanceCategory.class)
                .map(list -> Maps.uniqueIndex(list, AttendanceCategory::id))
                .flatMap(this::makeEnrichedAttendances);
    }

    public Single<List<ImmutableFullAnnouncement>> findFullAnnouncements() {
        return findAll(Announcement.class)
                .flattenAsObservable(l -> l)
                .flatMap(this::findFullAnnouncement)
                .toList();
    }

    private Observable<ImmutableFullAnnouncement> findFullAnnouncement(Announcement announcement) {
        return findByKey(Teacher.class, announcement.addedById())
                .map(teacher -> ImmutableFullAnnouncement.builder()
                        .from(announcement)
                        .addedBy(teacher)
                        .build())
                .toObservable();
    }

    public Observable<ImmutableEnrichedGrade> findEnrichedGrades() {
        return findFullGradeCategories()
                .flatMap(categories -> findAll(Grade.class)
                        .flattenAsObservable(l -> l)
                        .map(grade -> ImmutableEnrichedGrade.builder()
                                .from(grade)
                                .category(categories.get(grade.categoryId()))
                                .build())
                        .toList())
                .flattenAsObservable(l -> l);
    }

    public Single<Map<String, ImmutableFullGradeCategory>> findFullGradeCategories() {
        return findAllColors()
                .flatMap(colorMap -> findAll(GradeCategory.class)
                        .flattenAsObservable(l -> l)
                        .map(category -> ImmutableFullGradeCategory.builder()
                                .from(category)
                                .color(category.colorId().transform(colorMap::get))
                                .build())
                        .toMap(ImmutableFullGradeCategory::id));
    }

    private Single<Map<String, LibrusColor>> findAllColors() {
        return findAll(LibrusColor.class)
                .flattenAsObservable(l -> l)
                .toMap(LibrusColor::id);
    }

    public Single<List<GradeComment>> findGradeComments(List<String> ids) {
        return Observable.fromIterable(ids)
                .filter(id -> !id.isEmpty())
                .flatMap(id -> findByKey(GradeComment.class, id).toObservable())
                .toList();
    }

    public Single<ImmutableFullGrade> findFullGrade(EnrichedGrade grade) {
        return Single.zip(
                findByKey(Teacher.class, grade.addedById()),
                findGradeComments(grade.commentIds()),
                findByKey(Subject.class, grade.subjectId()),
                (t, cs, s) -> ImmutableFullGrade.builder()
                        .from(grade)
                        .category(grade.category())
                        .addedBy(t)
                        .subject(s)
                        .comments(cs)
                        .build()
        );
    }

    public Single<List<ImmutableFullSubject>> findFullSubjects() {
        return findAll(Average.class)
                .map(averages -> Maps.uniqueIndex(averages, Average::subject))
                .flatMap(averageMap -> findAll(Subject.class)
                        .flattenAsObservable(l -> l)
                        .map(subject -> ImmutableFullSubject.builder()
                                .from(subject)
                                .average(Optional.fromNullable(averageMap.get(subject.id())))
                                .build())
                        .toList());
    }

    public ReactiveEntityStore<Persistable> getDataStore() {
        return dataStore;
    }
}
