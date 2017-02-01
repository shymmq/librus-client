package pl.librus.client.datamodel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

/**
 * Created by Adam on 13.12.2016.
 */

@DatabaseTable(tableName = "attendances")
public class Attendance {
    @DatabaseField(id = true)
    private String id;

    @DatabaseField
    private HasId lesson, student, type, addedBy;

    @DatabaseField
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    @DatabaseField
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime addDate;

    @DatabaseField
    @JsonProperty("LessonNo")
    private int lessonNumber;
    @DatabaseField
    private int semester;

    public Attendance() {
    }

    public HasId getLesson() {
        return lesson;
    }

    public HasId getStudent() {
        return student;
    }

    public HasId getType() {
        return type;
    }

    public HasId getAddedBy() {
        return addedBy;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalDateTime getAddDate() {
        return addDate;
    }

    public int getLessonNumber() {
        return lessonNumber;
    }

    public int getSemester() {
        return semester;
    }

    public String getId() {
        return id;
    }
}
