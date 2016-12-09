package pl.librus.client.api;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.io.Serializable;

/**
 * Created by szyme on 08.12.2016. librus-client
 */
public class Grade implements Serializable {
    private static final long serialVersionUID = 2956642488287714235L;
    private String id, grade, lessonId, subjectId, categoryId, addedById;
    private int semester;
    private LocalDate date;
    private LocalDateTime addDate;
    private Type type;

    Grade(String id,
          String grade,
          String lessonId,
          String subjectId,
          String categoryId,
          String addedById,
          int semester,
          LocalDate date,
          LocalDateTime addDate,
          Type type) {
        this.id = id;
        this.grade = grade;
        this.lessonId = lessonId;
        this.subjectId = subjectId;
        this.categoryId = categoryId;
        this.addedById = addedById;
        this.semester = semester;
        this.date = date;
        this.addDate = addDate;
        this.type = type;
    }
//    List<String> commentIds;

    public String getId() {
        return id;
    }

    public String getGrade() {
        return grade;
    }

    public String getLessonId() {
        return lessonId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getAddedById() {
        return addedById;
    }

    public int getSemester() {
        return semester;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalDateTime getAddDate() {
        return addDate;
    }

    public Type getType() {
        return type;
    }

    enum Type {
        NORMAL, SEMESTER_PROPOSITION, SEMESTER, FINAL_PROPOSITION, FINAL
    }
}
