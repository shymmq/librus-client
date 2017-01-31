package pl.librus.client.cache;

import java.io.Serializable;
import java.util.List;

import pl.librus.client.api.Average;
import pl.librus.client.api.TextGrade;
import pl.librus.client.datamodel.Grade;
import pl.librus.client.datamodel.GradeComment;

/**
 * Created by szyme on 21.01.2017.
 */

public class GradeCache implements Serializable {
    private static final long serialVersionUID = 2773607494186776232L;
    private final List<Grade> grades;
    private final List<TextGrade> textGrades;
    private final List<Average> averages;
    private final List<GradeComment> comments;

    GradeCache(List<Grade> grades, List<TextGrade> textGrades, List<Average> averages, List<GradeComment> comments) {
        this.grades = grades;
        this.textGrades = textGrades;
        this.averages = averages;
        this.comments = comments;
    }

    public List<GradeComment> getComments() {
        return comments;
    }

    public List<Average> getAverages() {
        return averages;
    }

    public List<TextGrade> getTextGrades() {
        return textGrades;
    }

    public List<Grade> getGrades() {
        return grades;
    }
}
