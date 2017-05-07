package pl.librus.client.data;

import com.google.common.base.Optional;

import org.joda.time.LocalDate;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import pl.librus.client.UserScope;
import pl.librus.client.domain.Average;
import pl.librus.client.domain.LibrusClass;
import pl.librus.client.domain.LibrusColor;
import pl.librus.client.domain.LibrusUnit;
import pl.librus.client.domain.LuckyNumber;
import pl.librus.client.domain.Me;
import pl.librus.client.domain.PlainLesson;
import pl.librus.client.domain.Teacher;
import pl.librus.client.domain.announcement.Announcement;
import pl.librus.client.domain.announcement.FullAnnouncement;
import pl.librus.client.domain.attendance.Attendance;
import pl.librus.client.domain.attendance.AttendanceCategory;
import pl.librus.client.domain.attendance.FullAttendance;
import pl.librus.client.domain.event.Event;
import pl.librus.client.domain.event.EventCategory;
import pl.librus.client.domain.event.FullEvent;
import pl.librus.client.domain.grade.EnrichedGrade;
import pl.librus.client.domain.grade.FullGrade;
import pl.librus.client.domain.grade.Grade;
import pl.librus.client.domain.grade.GradeCategory;
import pl.librus.client.domain.lesson.EnrichedLesson;
import pl.librus.client.domain.lesson.FullLesson;
import pl.librus.client.domain.lesson.Lesson;
import pl.librus.client.domain.subject.FullSubject;
import pl.librus.client.domain.subject.Subject;

@UserScope
public class LibrusData {

    private final ServerFallbackStrategy strategy;

    @Inject
    public LibrusData(ServerFallbackStrategy strategy) {
        this.strategy = strategy;
    }

    public Observable<Lesson> findLessonsForWeek(LocalDate weekStart) {
        return strategy.getLessonsForWeek(weekStart);
    }

    public Single<Me> findMe() {
        return strategy.getAll(Me.class)
                .singleOrError();
    }

    public Single<LibrusUnit> findUnit() {
        return findMe()
                .map(Me::classId)
                .flatMap(classId -> strategy.getById(LibrusClass.class, classId))
                .map(LibrusClass::unit)
                .flatMap(unitId -> strategy.getById(LibrusUnit.class, unitId));
    }

    public Single<Optional<LuckyNumber>> findLuckyNumber() {
        return strategy.getAll(LuckyNumber.class)
                .lastElement()
                .map(Optional::of)
                .toSingle(Optional.absent());
    }

    public Observable<FullAttendance> findFullAttendances() {
        return BlockingLibrusData.preload(strategy,
                Attendance.class,
                AttendanceCategory.class,
                PlainLesson.class,
                Subject.class,
                Teacher.class)
                .flattenAsObservable(BlockingLibrusData::findFullAttendances);
    }

    public Observable<FullAnnouncement> findFullAnnouncements() {
        return BlockingLibrusData.preload(strategy, Teacher.class, Announcement.class)
                .flattenAsObservable(BlockingLibrusData::findFullAnnouncements);
    }

    public Observable<EnrichedGrade> findEnrichedGrades() {
        return BlockingLibrusData.preload(strategy,
                Grade.class,
                LibrusColor.class,
                GradeCategory.class)
                .flattenAsObservable(BlockingLibrusData::findEnrichedGrades);
    }

    public Observable<FullSubject> findFullSubjects() {
        return BlockingLibrusData.preload(strategy, Average.class, Subject.class)
                .flattenAsObservable(BlockingLibrusData::findFullSubjects);
    }

    public Observable<FullEvent> findFullEvents() {
        return BlockingLibrusData.preload(strategy, Event.class, EventCategory.class, Teacher.class)
                .flattenAsObservable(BlockingLibrusData::findFullEvents);
    }

    public Single<FullGrade> makeFullGrade(EnrichedGrade grade) {
        return BlockingLibrusData.preload(strategy)
                .map(data -> data.makeFullGrade(grade));
    }

    public Single<FullLesson> makeFullLesson(EnrichedLesson lesson) {
        return BlockingLibrusData.preload(strategy)
                .map(data -> data.makeFullLesson(lesson));
    }

}
