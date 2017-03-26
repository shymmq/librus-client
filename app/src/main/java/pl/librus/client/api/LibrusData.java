package pl.librus.client.api;

import android.content.Context;
import android.preference.PreferenceManager;

import com.google.common.base.Preconditions;

import org.joda.time.LocalDate;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import pl.librus.client.datamodel.Average;
import pl.librus.client.datamodel.LibrusColor;
import pl.librus.client.datamodel.LuckyNumber;
import pl.librus.client.datamodel.Me;
import pl.librus.client.datamodel.Teacher;
import pl.librus.client.datamodel.announcement.Announcement;
import pl.librus.client.datamodel.announcement.FullAnnouncement;
import pl.librus.client.datamodel.attendance.Attendance;
import pl.librus.client.datamodel.attendance.AttendanceCategory;
import pl.librus.client.datamodel.attendance.BaseAttendance;
import pl.librus.client.datamodel.attendance.EnrichedAttendance;
import pl.librus.client.datamodel.attendance.FullAttendance;
import pl.librus.client.datamodel.grade.EnrichedGrade;
import pl.librus.client.datamodel.grade.FullGrade;
import pl.librus.client.datamodel.grade.Grade;
import pl.librus.client.datamodel.grade.GradeCategory;
import pl.librus.client.datamodel.lesson.Lesson;
import pl.librus.client.datamodel.subject.FullSubject;
import pl.librus.client.datamodel.subject.Subject;

public class LibrusData {

    private final ServerFallbackStrategy strategy;

    private LibrusData(DatabaseStrategy databaseStrategy, IAPIClient serverStrategy) {
        this.strategy = new ServerFallbackStrategy(serverStrategy, databaseStrategy);
    }

    public static LibrusData getInstance(DatabaseStrategy databaseStrategy, IAPIClient serverStrategy) {
        return new LibrusData(databaseStrategy, serverStrategy);
    }

    public static LibrusData getInstance(Context context) {
        IAPIClient serverStrategy = new APIClient(context);
        DatabaseStrategy databaseStrategy = DatabaseStrategy.getInstance(context);

        return getInstance(databaseStrategy, serverStrategy);
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

    public Observable<EnrichedAttendance> findEnrichedAttendances() {
        return BlockingLibrusData.preload(strategy,
                Attendance.class,
                AttendanceCategory.class)
                .flattenAsObservable(BlockingLibrusData::findEnrichedAttendances);
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
