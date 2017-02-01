package pl.librus.client.datamodel;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

@DatabaseTable(tableName = "timetable_lessons")
public class Lesson implements Comparable<Lesson> {
    public static final String COLUMN_NAME_DATE = "date";

    @JsonProperty("LessonNo")
    @DatabaseField
    private int lessonNumber;
    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private Subject subject;
    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private Teacher teacher;
    @DatabaseField
    private String id;
    @DatabaseField(id = true)
    private String uniqueId;
    @DatabaseField
    private String orgSubjectId;
    @DatabaseField
    private String orgTeacherId;
    @DatabaseField
    private boolean isSubstitutionClass;
    @DatabaseField
    private boolean isCanceled;
    @DatabaseField(columnName = COLUMN_NAME_DATE)
    private LocalDate date;
    @DatabaseField
    private LocalTime startTime;
    @DatabaseField
    private LocalTime endTime;

    //for moved lessons
    @DatabaseField
    private String newSubjectId;
    @DatabaseField
    private String newTeacherId;
    @DatabaseField
    private LocalDate newDate;
    @DatabaseField
    private int newLessonNo;

    public Lesson() {
    }

    public Lesson(int lessonNumber, Subject subject, Teacher teacher, String id, String orgSubjectId, String orgTeacherId, boolean isSubstitutionClass, boolean isCanceled, LocalDate date, LocalTime startTime, LocalTime endTime, String newSubjectId, String newTeacherId, LocalDate newDate, int newLessonNo) {
        this.lessonNumber = lessonNumber;
        this.subject = subject;
        this.teacher = teacher;
        this.id = id;
        this.orgSubjectId = orgSubjectId;
        this.orgTeacherId = orgTeacherId;
        this.isSubstitutionClass = isSubstitutionClass;
        this.isCanceled = isCanceled;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.newSubjectId = newSubjectId;
        this.newTeacherId = newTeacherId;
        this.newDate = newDate;
        this.newLessonNo = newLessonNo;
        this.uniqueId = id + "-" + lessonNumber + "-" + date.getDayOfMonth() + "-" + date.getYearOfCentury();
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

    public String getId() {
        return id;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getOrgSubjectId() {
        return orgSubjectId;
    }

    public String getOrgTeacherId() {
        return orgTeacherId;
    }

    public boolean isSubstitutionClass() {
        return isSubstitutionClass;
    }

    public boolean isCanceled() {
        return isCanceled;
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

    public String getNewSubjectId() {
        return newSubjectId;
    }

    public String getNewTeacherId() {
        return newTeacherId;
    }

    public LocalDate getNewDate() {
        return newDate;
    }

    public int getNewLessonNo() {
        return newLessonNo;
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
