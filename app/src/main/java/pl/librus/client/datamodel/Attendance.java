package pl.librus.client.datamodel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

/**
 * Created by Adam on 13.12.2016.
 */

public class Attendance extends HasId {

    private HasId lesson, student, type, addedBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime addDate;

    @JsonProperty("LessonNo")
    private int lessonNumber;
    private int semester;

    public Attendance() {
    }

    public Attendance(String id, String lessonId, String studentId, LocalDate date, LocalDateTime addDate, int lessonNumber, int semester, String typeId, String addedById) {
        this.id = id;

        this.lesson = new HasId(lessonId);
        this.student = new HasId(studentId);
        this.type = new HasId(typeId);
        this.addedBy = new HasId(addedById);

        this.date = date;
        this.addDate = addDate;
        this.lessonNumber = lessonNumber;
        this.semester = semester;
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
}
