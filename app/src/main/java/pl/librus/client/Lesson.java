package pl.librus.client;

import android.util.Log;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;


class Lesson implements Serializable {


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
                try {
                    String orgTeacherId = data.getJSONObject("OrgTeacher").getString("Id");
                    this.orgTeacher = new Teacher(orgTeacherId, orgTeacherId, "");
                    this.orgSubject = new Subject(data.getJSONObject("OrgSubject").getString("Id"));
                } catch (JSONException e) {
                    Log.d(TAG, "JSONException : " + date.toString() + " " + "Lesson " + lessonNumber + " " + subject.getName());
                }
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
