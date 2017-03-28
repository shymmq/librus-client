package pl.librus.client.data.server;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
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

import static com.google.common.collect.Lists.newArrayList;

@Singleton
public class APIClient implements IAPIClient {

    private final MockEntityRepository repository = new MockEntityRepository();
    private final EntityMocks templates = new EntityMocks();

    @Inject
    public APIClient() {
    }

    public Single<String> login(String username, String password) {
        return Single.just(username);
    }

    public Observable<Lesson> getLessonsForWeek(LocalDate weekStart) {
        Timetable result = new Timetable();
        for (int dayNo = DateTimeConstants.MONDAY; dayNo <= DateTimeConstants.FRIDAY; dayNo++) {
            List<List<JsonLesson>> schoolDay = new ArrayList<>();
            for (int lessonNo = 0; lessonNo < repository.getList(PlainLesson.class).size(); lessonNo++) {
                ImmutableJsonLesson lesson = templates.jsonLesson()
                        .withDayNo(dayNo);
                schoolDay.add(newArrayList(withLessonNumber(lesson, lessonNo)));
            }
            result.put(weekStart.plusDays(dayNo - 1), schoolDay);
        }
        //weekend
        result.put(weekStart.plusDays(5), newArrayList());
        result.put(weekStart.plusDays(6), newArrayList());

        result.get(weekStart).add(newArrayList(
                withLessonNumber(cancelledLesson(), 7)
        ));

        result.get(weekStart.plusDays(1)).add(newArrayList(
                withLessonNumber(substitutionLesson(), 7)
        ));

        //Empty lesson
        result.get(weekStart.plusDays(2)).remove(2);

        return Observable.fromIterable(result.toLessons());
    }

    private ImmutableJsonLesson withLessonNumber(ImmutableJsonLesson lesson, int lessonNo) {
        List<PlainLesson> plainLessons = repository.getList(PlainLesson.class);
        PlainLesson plainLesson = plainLessons.get(lessonNo % plainLessons.size());
        Subject subject = getById(Subject.class, plainLesson.subject()).blockingGet();
        Teacher teacher = getById(Teacher.class, plainLesson.teacher()).blockingGet();

        LocalTime startTime = LocalTime.parse("08:00");
        LocalTime lessonStart = startTime.plusHours(lessonNo);
        LocalTime lessonEnd = lessonStart.plusMinutes(45);

        return lesson
                .withSubject(LessonSubject.fromSubject(subject))
                .withTeacher(LessonTeacher.fromTeacher(teacher))
                .withLessonNo(lessonNo)
                .withHourFrom(lessonStart)
                .withHourTo(lessonEnd);
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
                .withOrgTeacher(repository.getList(Teacher.class).get(3).id())
                .withOrgSubject(repository.getList(Subject.class).get(3).id())
                .withOrgDate(LocalDate.parse("2017-01-01"));
    }

    public <T extends Persistable> Observable<T> getAll(Class<T> clazz) {
        EntityInfo info = EntityInfos.infoFor(clazz);
        if (info.single()) {
            //noinspection unchecked
            return getObject(info.endpoint(), info.topLevelName(), clazz)
                    .toObservable();
        } else {
            return getAll(info.endpoint(), info.topLevelName(), clazz);
        }
    }

    public <T> Single<T> getObject(String endpoint, String topLevelName, Class<T> clazz) {
        return Single.fromCallable(() -> repository.getList(clazz).get(0))
                .subscribeOn(Schedulers.computation());
    }

    public <T> Observable<T> getAll(String endpoint, String topLevelName, Class<T> clazz) {
        return Observable.fromCallable(() -> repository.getList(clazz))
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
}
