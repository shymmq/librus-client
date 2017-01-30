package pl.librus.client.api;

import android.support.annotation.NonNull;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.io.Serializable;


public class Lesson implements Serializable, Comparable<Lesson> {


    private static final long serialVersionUID = 3925316087529938003L;
    private final int lessonNumber;
    private final Subject subject;
    private final Teacher teacher;
    private final String id, orgSubjectId, orgTeacherId;
    private final boolean isSubstitutionClass, isCanceled;
    private final LocalDate date;
    private final LocalTime startTime, endTime;

    //for moved lessons
    private final String newSubjectId, newTeacherId;
    private final LocalDate newDate;
    private final int newLessonNo;

    //normal lesson
    public Lesson(String id,
                  int lessonNumber, LocalDate date, LocalTime startTime, LocalTime endTime,
                  Subject subject, Teacher teacher) {
        this.id = id;
        this.lessonNumber = lessonNumber;
        this.subject = subject;
        this.teacher = teacher;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isSubstitutionClass = false;
        this.isCanceled = false;
        this.orgSubjectId = this.orgTeacherId = this.newSubjectId = this.newTeacherId = null;
        this.newDate = null;
        this.newLessonNo = -1;
    }

    //substitution
    public Lesson(String id,
                  int lessonNumber, LocalDate date, LocalTime startTime, LocalTime endTime,
                  Subject subject, Teacher teacher,
                  String orgSubjectId, String orgTeacherId) {
        this.id = id;
        this.lessonNumber = lessonNumber;
        this.subject = subject;
        this.teacher = teacher;
        this.orgSubjectId = orgSubjectId;
        this.orgTeacherId = orgTeacherId;
        this.isSubstitutionClass = true;
        this.isCanceled = false;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.newSubjectId = this.newTeacherId = null;
        this.newDate = null;
        this.newLessonNo = -1;
    }

    //canceled
    public Lesson(String id,
                  int lessonNumber, LocalDate date, LocalTime startTime, LocalTime endTime,
                  Subject subject, Teacher teacher,
                  boolean isCanceled) {
        this.id = id;
        this.lessonNumber = lessonNumber;
        this.subject = subject;
        this.teacher = teacher;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isSubstitutionClass = false;
        this.isCanceled = isCanceled;
        this.orgSubjectId = this.orgTeacherId = this.newSubjectId = this.newTeacherId = null;
        this.newDate = null;
        this.newLessonNo = -1;
    }

    //moved
    Lesson(String id,
           int lessonNumber, LocalDate date, LocalTime startTime, LocalTime endTime,
           Subject subject, Teacher teacher,
           String newSubjectId, String newTeacherId,
           int newLessonNo, LocalDate newDate) {
        this.id = id;
        this.lessonNumber = lessonNumber;
        this.subject = subject;
        this.teacher = teacher;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isSubstitutionClass = false;
        this.isCanceled = false;
        this.orgSubjectId = this.orgTeacherId = null;
        this.newDate = newDate;
        this.newLessonNo = newLessonNo;
        this.newSubjectId = newSubjectId;
        this.newTeacherId = newTeacherId;
    }

    public String getOrgSubjectId() {
        return orgSubjectId;
    }

    public String getOrgTeacherId() {
        return orgTeacherId;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public int getLessonNumber() {
        return lessonNumber;
    }

    public Subject getSubject() {
        return subject;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public boolean isSubstitutionClass() {
        return isSubstitutionClass;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public String getId() {
        return id;
    }

    public String getUniqueId() {
        return id + "-" + lessonNumber + "-" + date.getDayOfMonth() + "-" + date.getYearOfCentury();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Lesson lesson = (Lesson) o;

        return lessonNumber == lesson.lessonNumber && id.equals(lesson.id) && date.equals(lesson.date);

    }

    @Override
    public int hashCode() {
        int result = lessonNumber;
        result = 31 * result + id.hashCode();
        result = 31 * result + date.hashCode();
        return result;
    }

    public int getNewLessonNo() {
        return newLessonNo;
    }

    public LocalDate getNewDate() {
        return newDate;
    }

    public String getNewSubjectId() {
        return newSubjectId;
    }

    public String getNewTeacherId() {
        return newTeacherId;
    }

    @Override
    public int compareTo(@NonNull Lesson o) {
        return Integer.compare(lessonNumber, o.lessonNumber);
    }
}
