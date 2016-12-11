package pl.librus.client.api;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.io.Serializable;


public class Lesson implements Serializable {

    private static final long serialVersionUID = -6693067437113468425L;
    private int lessonNumber = 0;
    private Event event = null;
    private Subject subject;
    private Teacher teacher;
    private Subject orgSubject = null;
    private Teacher orgTeacher = null;
    private boolean substitution = false;
    private boolean isCanceled;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    Lesson(int lessonNumber, LocalDate date, LocalTime startTime, LocalTime endTime,
           Subject subject, Teacher teacher,
           boolean isCanceled,
           boolean substitution,
           Subject orgSubject, Teacher orgTeacher,
           Event event) {
        this.lessonNumber = lessonNumber;
        this.event = event;
        this.subject = subject;
        this.teacher = teacher;
        this.orgSubject = orgSubject;
        this.orgTeacher = orgTeacher;
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

    public Subject getOrgSubject() {
        return orgSubject;
    }

    public Teacher getOrgTeacher() {
        return orgTeacher;
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
}
