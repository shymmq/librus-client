package pl.librus.client.data.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import java8.util.stream.StreamSupport;
import pl.librus.client.data.EntityInfo;
import pl.librus.client.data.EntityInfos;
import pl.librus.client.domain.Identifiable;
import pl.librus.client.domain.PlainLesson;
import pl.librus.client.domain.Teacher;
import pl.librus.client.domain.lesson.ImmutableJsonLesson;
import pl.librus.client.domain.lesson.JsonLesson;
import pl.librus.client.domain.lesson.Lesson;
import pl.librus.client.domain.lesson.LessonSubject;
import pl.librus.client.domain.lesson.LessonTeacher;
import pl.librus.client.domain.lesson.Timetable;
import pl.librus.client.domain.subject.Subject;
import pl.librus.client.util.LibrusUtils;

import static com.google.common.collect.Lists.newArrayList;

@Singleton
public class APIClient implements IAPIClient {

    private final MockEntityRepository repository = new MockEntityRepository();
    private final EntityMocks templates = new EntityMocks();

    private final AtomicInteger lessonLoadCount = new AtomicInteger();

    private Map<LocalDate, Timetable> lessonsForWeeks = Maps.newHashMap();

    @Inject
    public APIClient() {
    }

    public Single<String> login(String username, String password) {
        return Single.just(username);
    }

    public Observable<Lesson> getLessonsForWeek(LocalDate weekStart) {
        Timetable timetable = lessonsForWeeks.get(weekStart);
        if (timetable == null) {
            timetable = createLessonsForWeek(weekStart);
            lessonsForWeeks.put(weekStart, timetable);
        }

        //Sometimes substitution lesson
        timetable = timetable.shallowCopy();
        LocalDate date = weekStart.plusDays(3);
        List<List<JsonLesson>> day = Lists.newArrayList(timetable.get(date));
        int lessonNo = 3;
        JsonLesson lesson = lessonLoadCount.incrementAndGet() % 6 == 0 ? substitutionLesson() : day.get(lessonNo).get(0);
        day.set(lessonNo, withLessonNumber(lesson, lessonNo));
        timetable.put(date, day);

        return Observable.fromIterable(timetable.toLessons())
                .doOnSubscribe(d -> LibrusUtils.log("Fetching lessons"))
                .subscribeOn(Schedulers.computation());
    }

    private Timetable createLessonsForWeek(LocalDate weekStart) {
        Timetable result = new Timetable();
        for (int dayNo = DateTimeConstants.MONDAY; dayNo <= DateTimeConstants.FRIDAY; dayNo++) {
            List<List<JsonLesson>> schoolDay = new ArrayList<>();
            for (int lessonNo = 0; lessonNo < repository.getList(PlainLesson.class).size(); lessonNo++) {
                ImmutableJsonLesson lesson = templates.jsonLesson()
                        .withDayNo(dayNo);
                schoolDay.add(withLessonNumber(lesson, lessonNo));
            }
            result.put(weekStart.plusDays(dayNo - 1), schoolDay);
        }
        //weekend
        result.put(weekStart.plusDays(5), newArrayList());
        result.put(weekStart.plusDays(6), newArrayList());

        result.get(weekStart).add(withLessonNumber(cancelledLesson(), 7));

        result.get(weekStart.plusDays(1)).add(withLessonNumber(substitutionLesson(), 7));

        //Empty lesson
        result.get(weekStart.plusDays(2)).remove(2);

        return result;
    }

    private List<JsonLesson> withLessonNumber(JsonLesson lesson, int lessonNo) {
        PlainLesson plainLesson = getPlainLesson(lessonNo);

        LocalTime startTime = LocalTime.parse("08:00");
        LocalTime lessonStart = startTime.plusHours(lessonNo);
        LocalTime lessonEnd = lessonStart.plusMinutes(45);

        JsonLesson res = ImmutableJsonLesson.copyOf(lesson)
                .withSubject(getLessonSubject(plainLesson))
                .withTeacher(getLessonTeacher(plainLesson))
                .withLessonNo(lessonNo)
                .withHourFrom(lessonStart)
                .withHourTo(lessonEnd);
        return Lists.newArrayList(res);
    }

    private PlainLesson getPlainLesson(int lessonNo) {
        List<PlainLesson> plainLessons = repository.getList(PlainLesson.class);
        return plainLessons.get(lessonNo % plainLessons.size());
    }

    private LessonTeacher getLessonTeacher(PlainLesson plainLesson) {
        Teacher teacher = fromRepoById(Teacher.class, plainLesson.teacher());
        return LessonTeacher.fromTeacher(teacher);
    }

    private LessonSubject getLessonSubject(PlainLesson plainLesson) {
        Subject subject = fromRepoById(Subject.class, plainLesson.subject());
        return LessonSubject.fromSubject(subject);
    }

    private ImmutableJsonLesson cancelledLesson() {
        return templates.jsonLesson()
                .withSubject(templates.lessonSubject().withName("Alchemia"))
                .withCancelled(true);
    }

    private ImmutableJsonLesson substitutionLesson() {
        return templates.jsonLesson()
                .withTeacher(templates.lessonTeacher()
                        .withFirstName("Michał")
                        .withLastName("Oryginalny"))
                .withSubject(templates.lessonSubject().withName("Marynowanie śledzi"))
                .withSubstitutionClass(true)
                .withSubstitutionNote("Zabrakło śledzi")
                .withOrgTeacherId(repository.getList(Teacher.class).get(3).id())
                .withOrgSubject(repository.getList(Subject.class).get(3).id())
                .withOrgDate(LocalDate.parse("2017-01-01"));
    }

    public <T extends Persistable> Observable<T> getAll(Class<T> clazz) {
        EntityInfo info = EntityInfos.infoFor(clazz);
        if (info.single()) {
            //noinspection unchecked
            return getObject(info.endpoint(), clazz)
                    .toObservable();
        } else {
            return getAll(info.endpoint(), clazz);
        }
    }

    public <T> Single<T> getObject(String endpoint, Class<T> clazz) {
        return Single.fromCallable(() -> repository.getList(clazz).get(0))
                .doOnSubscribe(d -> LibrusUtils.log("Fetching %s", clazz.getSimpleName()))
                .subscribeOn(Schedulers.computation());
    }

    public <T> Observable<T> getAll(String endpoint, Class<T> clazz) {
        return Observable.fromCallable(() -> repository.getList(clazz))
                .doOnSubscribe(d -> LibrusUtils.log("Fetching %s", clazz.getSimpleName()))
                .flatMapIterable(i -> i)
                .subscribeOn(Schedulers.computation());
    }

    @Override
    public Completable pushDevices(String regToken) {
        return Completable.complete();
    }

    @Override
    public <T extends Persistable & Identifiable> Single<T> getById(Class<T> clazz, String id) {
        return getAll(clazz)
                .filter(e -> e.id().equals(id))
                .firstOrError();
    }

    private <T extends Persistable & Identifiable> T fromRepoById(Class<T> clazz, String id) {
        return StreamSupport.stream(repository.getList(clazz))
                .filter(e -> e.id().equals(id))
                .findFirst()
                .get();
    }
}
