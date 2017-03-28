package pl.librus.client.data;

import org.joda.time.LocalDate;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import pl.librus.client.UserScope;
import pl.librus.client.domain.Average;
import pl.librus.client.domain.LibrusColor;
import pl.librus.client.domain.LuckyNumber;
import pl.librus.client.domain.Me;
import pl.librus.client.domain.PlainLesson;
import pl.librus.client.domain.Teacher;
import pl.librus.client.domain.announcement.Announcement;
import pl.librus.client.domain.announcement.FullAnnouncement;
import pl.librus.client.domain.attendance.Attendance;
import pl.librus.client.domain.attendance.AttendanceCategory;
import pl.librus.client.domain.attendance.FullAttendance;
import pl.librus.client.domain.grade.EnrichedGrade;
import pl.librus.client.domain.grade.Grade;
import pl.librus.client.domain.grade.GradeCategory;
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

    public Maybe<LuckyNumber> findLuckyNumber() {
        return strategy.getAll(LuckyNumber.class)
                .lastElement();
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

    public BlockingLibrusData blocking() {
        return BlockingLibrusData.get(strategy);
    }

}
