package pl.librus.client.api;

import android.support.annotation.NonNull;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.io.Serializable;

import pl.librus.client.datamodel.Grade;
import pl.librus.client.grades.GradeEntry;

/**
 * Created by szyme on 11.12.2016. librus-client
 */

public class TextGrade extends GradeEntry implements Serializable, Comparable {

    private static final long serialVersionUID = 7711525769532900327L;

    private String id, lessonId, subjectId, addedById, categoryId, grade;
    private int semester;
    private LocalDate date;
    private LocalDateTime addDate;
    private Type type;

    public TextGrade(String id,
                     String grade,
                     String lessonId,
                     String subjectId,
                     String categoryId,
                     String addedById,
                     int semester,
                     LocalDate date,
                     LocalDateTime addDate,
                     Type type) {
        this.type = type;
        this.addDate = addDate;
        this.date = date;
        this.semester = semester;
        this.grade = grade;
        this.addedById = addedById;
        this.categoryId = categoryId;
        this.subjectId = subjectId;
        this.lessonId = lessonId;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getLessonId() {
        return lessonId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public String getAddedById() {
        return addedById;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getGrade() {
        return grade;
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

    @Override
    public int compareTo(@NonNull Object o) {
        if (o instanceof Average) {
            return -1;
        } else if (o instanceof Grade) {
            return date.compareTo(((Grade) o).getDate());
        } else if (o instanceof TextGrade) {
            return date.compareTo(((TextGrade) o).getDate());
        } else {
            return 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TextGrade textGrade = (TextGrade) o;

        return id.equals(textGrade.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    enum Type {
        NORMAL, SEMESTER_PROPOSITION, SEMESTER, FINAL_PROPOSITION, FINAL
    }
}
