package pl.librus.client.api;

import android.content.Context;
import android.util.ArrayMap;

import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibrusData implements Serializable {
    private static final long serialVersionUID = 9103658319690261655L;

    private static final String TAG = "librus-client-log";
    private final long timestamp;
    transient private Context context;
    private boolean debug = true;

    private List<SchoolWeek> schoolWeeks = new ArrayList<>();            //schoolWeek
    private List<Event> events = new ArrayList<>();

    private List<Grade> grades = new ArrayList<>();              //grades
    private List<GradeComment> gradeComments = new ArrayList<>();
    private List<TextGrade> textGrades = new ArrayList<>();
    private List<Average> averages = new ArrayList<>();

    private List<Announcement> announcements = new ArrayList<>();//other
    private List<Attendance> attendances;
    private List<AttendanceCategory> attendanceCategories;
    private LuckyNumber luckyNumber;

    //Persistent data:
    private List<Teacher> teachers;
    private List<Subject> subjects;
    private List<PlainLesson> plainLessons;
    private List<EventCategory> eventCategories;
    private List<GradeCategory> gradeCategories;
    private LibrusAccount account;

    public LibrusData(Context context) {
        this.context = context;
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<SchoolWeek> getSchoolWeeks() {
        return schoolWeeks;
    }

    void setSchoolWeeks(List<SchoolWeek> schoolWeeks) {
        this.schoolWeeks = schoolWeeks;
    }

    public List<Event> getEvents() {
        return events;
    }

    void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }

    public List<GradeComment> getGradeComments() {
        return gradeComments;
    }

    public void setGradeComments(List<GradeComment> gradeComments) {
        this.gradeComments = gradeComments;
    }

    public List<TextGrade> getTextGrades() {
        return textGrades;
    }

    public void setTextGrades(List<TextGrade> textGrades) {
        this.textGrades = textGrades;
    }

    public List<Average> getAverages() {
        return averages;
    }

    public void setAverages(List<Average> averages) {
        this.averages = averages;
    }

    public List<Announcement> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(List<Announcement> announcements) {
        this.announcements = announcements;
    }

    public List<Attendance> getAttendances() {
        return attendances;
    }

    public void setAttendances(List<Attendance> attendances) {
        this.attendances = attendances;
    }

    public List<AttendanceCategory> getAttendanceCategories() {
        return attendanceCategories;
    }

    public void setAttendanceCategories(List<AttendanceCategory> attendanceCategories) {
        this.attendanceCategories = attendanceCategories;
    }

    public LuckyNumber getLuckyNumber() {
        return luckyNumber;
    }

    public void setLuckyNumber(LuckyNumber luckyNumber) {
        this.luckyNumber = luckyNumber;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    public List<PlainLesson> getPlainLessons() {
        return plainLessons;
    }

    public void setPlainLessons(List<PlainLesson> plainLessons) {
        this.plainLessons = plainLessons;
    }

    public List<EventCategory> getEventCategories() {
        return eventCategories;
    }

    public void setEventCategories(List<EventCategory> eventCategories) {
        this.eventCategories = eventCategories;
    }

    public List<GradeCategory> getGradeCategories() {
        return gradeCategories;
    }

    public void setGradeCategories(List<GradeCategory> gradeCategories) {
        this.gradeCategories = gradeCategories;
    }

    public LibrusAccount getAccount() {
        return account;
    }

    public void setAccount(LibrusAccount account) {
        this.account = account;
    }

    public Map<String, AttendanceCategory> getAttendanceCategoryMap() {
        Map<String, AttendanceCategory> res = new HashMap<>();
        for (AttendanceCategory ac : attendanceCategories) {
            res.put(ac.getId(), ac);
        }
        return res;
    }

    public Map<String, Teacher> getTeacherMap() {
        Map<String, Teacher> res = new HashMap<>(teachers.size());
        for (Teacher t : teachers) res.put(t.getId(), t);
        return res;
    }

    public Map<String, Subject> getSubjectMap() {
        Map<String, Subject> res = new HashMap<>();
        for (Subject s : subjects) {
            res.put(s.getId(), s);
        }
        return res;
    }

    public Map<String, String> getLessonMap() {
        Map<String, String> res = new HashMap<>();
        for (PlainLesson pl : plainLessons) {
            res.put(String.valueOf(pl.getId()), String.valueOf(pl.getSubjectId()));
        }
        return res;
    }

    public Map<String, EventCategory> getEventCategoriesMap() {
        Map<String, EventCategory> res = new HashMap<>();
        for (EventCategory e : eventCategories) res.put(e.getId(), e);
        return res;
    }

    public Map<String, GradeCategory> getGradeCategoriesMap() {
        Map<String, GradeCategory> res = new HashMap<>(gradeCategories.size());
        for (GradeCategory gc : gradeCategories) res.put(gc.getId(), gc);
        return res;
    }

    public Map<String, GradeComment> getCommentMap() {
        Map<String, GradeComment> res = new HashMap<>(gradeComments.size());
        for (GradeComment gc : gradeComments) res.put(gc.getId(), gc);
        return res;
    }

    public Map<String, String> getLessonSubjectMap() {
        Map<String, String> res = new HashMap<>();
        for (PlainLesson pl : plainLessons) res.put(pl.getId(), pl.getSubjectId());
        return res;
    }

    public Map<LocalDate, SchoolDay> getSchoolDayMap() {
        Map<LocalDate, SchoolDay> res = new ArrayMap<>();
        for (SchoolWeek w : schoolWeeks)
            for (SchoolDay d : w.getSchoolDays())
                res.put(d.getDate(), d);
        return res;
    }
}
