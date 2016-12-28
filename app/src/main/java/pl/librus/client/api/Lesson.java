package pl.librus.client.api;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.io.Serializable;


public class Lesson implements Serializable {


    private static final long serialVersionUID = 3925316087529938003L;
    private String id;
    private int lessonNumber = 0;
    private Event event = null;
    private Subject subject;
    private Teacher teacher;
    private String orgSubjectId = null;
    private String orgTeacherId = null;
    private boolean substitution;
    private boolean isCanceled;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    Lesson(String id,
           int lessonNumber, LocalDate date, LocalTime startTime, LocalTime endTime,
           Subject subject, Teacher teacher,
           boolean isCanceled,
           boolean substitution,
           String orgSubjectId, String orgTeacherId,
           Event event) {
        this.id = id;
        this.lessonNumber = lessonNumber;
        this.event = event;
        this.subject = subject;
        this.teacher = teacher;
        this.orgSubjectId = orgSubjectId;
        this.orgTeacherId = orgTeacherId;
        this.substitution = substitution;
        this.isCanceled = isCanceled;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
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

    public boolean isSubstitution() {
        return substitution;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public int getChanges(Lesson lesson) {
        return 0;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Lesson lesson = (Lesson) o;

        return id.equals(lesson.id) && date.equals(lesson.date);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + date.hashCode();
        return result;
    }
}
