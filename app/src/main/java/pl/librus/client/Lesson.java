package pl.librus.client;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;


public class Lesson implements Serializable {


    private final String TAG = "schedule:log";
    private Event event = null;
    private Subject subject;
    private Teacher teacher;
    private Subject orgSubject = null;
    private Teacher orgTeacher = null;
    private boolean substitution = false;
    private int lessonNumber;
    private boolean isCanceled;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    Lesson(JSONObject data, int lessonNumber, LocalDate date) throws JSONException, ParseException {
        this.lessonNumber = lessonNumber;
//        Log.d(TAG, "Creating lesson from JSON:   " + data.toString());
        if (data.length() > 0) {
            this.isCanceled = data.getBoolean("IsCanceled");
            this.subject = new Subject(data.getJSONObject("Subject"));
            this.teacher = new Teacher(data.getJSONObject("Teacher"));
            startTime = LocalTime.parse(data.getString("HourFrom"), DateTimeFormat.forPattern("HH:mm"));
            endTime = LocalTime.parse(data.getString("HourTo"), DateTimeFormat.forPattern("HH:mm"));
            this.date = date;
            this.substitution = data.getBoolean("IsSubstitutionClass");
            if (substitution) {
                this.orgTeacher = new Teacher(data.getJSONObject("orgTeacher"));
                this.orgSubject = new Subject(data.getJSONObject("orgSubject"));
            }
        }

    }

    public Lesson(Subject subject, Teacher teacher, int lessonNumber) {
        this.subject = subject;
        this.teacher = teacher;
        this.lessonNumber = lessonNumber;
    }


    Event getEvent() {
        return event;
    }

    void setEvent(Event event) {
        this.event = event;
    }

    Subject getOrgSubject() {
        return orgSubject;
    }

    Teacher getOrgTeacher() {
        return orgTeacher;
    }

    LocalDate getDate() {
        return date;
    }

    LocalTime getStartTime() {
        return startTime;
    }

    LocalTime getEndTime() {
        return endTime;
    }

    int getLessonNumber() {
        return lessonNumber;
    }

    Subject getSubject() {
        return subject;
    }

    Teacher getTeacher() {
        return teacher;
    }

    boolean isSubstitution() {
        return substitution;
    }

    boolean isCanceled() {
        return isCanceled;
    }
}
