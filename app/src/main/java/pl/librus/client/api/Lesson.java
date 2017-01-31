package pl.librus.client.api;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import pl.librus.client.datamodel.Subject;
import pl.librus.client.datamodel.Teacher;


public class Lesson implements Comparable<Lesson> {


    @JsonProperty("LessonNo")
    private int lessonNumber;
    private Subject subject;
    private Teacher teacher;
    private String id, orgSubjectId, orgTeacherId;
    private boolean isSubstitutionClass, isCanceled;
    private LocalDate date;
    private LocalTime startTime, endTime;

    //for moved lessons
    private String newSubjectId, newTeacherId;
    private LocalDate newDate;
    private int newLessonNo;

    public Lesson() {
    }

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

    @Override
    public String toString() {
        return "Lesson{" +
                "lessonNumber=" + lessonNumber +
                ", subject=" + subject +
                ", teacher=" + teacher +
                ", id='" + id + '\'' +
                ", orgSubjectId='" + orgSubjectId + '\'' +
                ", orgTeacherId='" + orgTeacherId + '\'' +
                ", isSubstitutionClass=" + isSubstitutionClass +
                ", isCanceled=" + isCanceled +
                ", date=" + date +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", newSubjectId='" + newSubjectId + '\'' +
                ", newTeacherId='" + newTeacherId + '\'' +
                ", newDate=" + newDate +
                ", newLessonNo=" + newLessonNo +
                '}';
    }
}
