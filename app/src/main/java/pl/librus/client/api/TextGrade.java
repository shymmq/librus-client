package pl.librus.client.api;

import android.support.annotation.NonNull;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import pl.librus.client.datamodel.Average;
import pl.librus.client.datamodel.Grade;
import pl.librus.client.grades.GradeEntry;

/**
 * Created by szyme on 11.12.2016. librus-client
 */

public class TextGrade extends GradeEntry implements Comparable {

    private final String id;
    private final String lessonId;
    private final String subjectId;
    private final String addedById;
    private final String categoryId;
    private final String grade;
    private final int semester;
    private final LocalDate date;
    private final LocalDateTime addDate;
    private final Type type;

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

    private LocalDate getDate() {
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
            return date.compareTo(((Grade) o).date());
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
