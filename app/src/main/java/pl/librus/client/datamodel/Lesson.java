package pl.librus.client.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

@DatabaseTable(tableName = "timetable_lessons")
public class Lesson {
    public static final String COLUMN_NAME_DATE = "date";
    @DatabaseField
    private HasId lesson;

    @DatabaseField
    private int lessonNo;

    @DatabaseField
    private int dayNo;

    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private Subject subject;

    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private Teacher teacher;

    @DatabaseField
    @JsonProperty("IsSubstitutionClass")
    private boolean substitution;

    @DatabaseField
    @JsonProperty("IsCanceled")
    private boolean canceled;

    @DatabaseField
    private LocalTime hourFrom, hourTo;

    @DatabaseField(columnName = COLUMN_NAME_DATE)
    private LocalDate date;

    @DatabaseField(useGetSet = true, id = true)
    private String uniqueId;

    public String getUniqueId() {
        if (date == null) {
            throw new IllegalStateException("Attempt to get id before setting date");
        } else {
            return lesson.id + lessonNo + dayNo + date.getWeekOfWeekyear();
        }
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public HasId getLesson() {
        return lesson;
    }

    public int getLessonNo() {
        return lessonNo;
    }

    public int getDayNo() {
        return dayNo;
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
        return canceled;
    }

    public LocalTime getHourFrom() {
        return hourFrom;
    }

    public LocalTime getHourTo() {
        return hourTo;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
