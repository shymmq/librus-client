package pl.librus.client.api;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.io.Serializable;

import pl.librus.client.grades.GradeEntry;

/**
 * Created by szyme on 08.12.2016. librus-client
 */
public class Grade extends GradeEntry<Grade> implements Serializable {
    private static final long serialVersionUID = 2956642488287714235L;
    private String id, grade, lessonId, subjectId, categoryId, addedById, commentId;
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
          String commentId,
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
        this.commentId = commentId;
        this.semester = semester;
        this.date = date;
        this.addDate = addDate;
        this.type = type;
    }

    public String getId() {
        return id;
    }
//    List<String> commentIds;

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

    public String getCommentId() {
        return commentId;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Grade grade = (Grade) o;

        return id.equals(grade.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public int compareTo(Grade o) {
        return date.compareTo(o.getDate());
    }

    //    @Override
//    public Change getChanges(Grade newGrade) {
//        if (!Objects.equals(grade, newGrade.getGrade())) {
//            //Grade value changed
//            return new Change(Change.Action.CHANGE, Change.ObjectType.GRADE_VALUE);
//        } else if (commentId == null && newGrade.getCommentId() != null) {
//            //Comment added
//            return new Change(Change.Action.ADD, Change.ObjectType.GRADE_COMMENT);
//        } else {
//            //Nothing important changed
//            return null;
//        }
//    }

    enum Type {
        NORMAL, SEMESTER_PROPOSITION, SEMESTER, FINAL_PROPOSITION, FINAL
    }
}
