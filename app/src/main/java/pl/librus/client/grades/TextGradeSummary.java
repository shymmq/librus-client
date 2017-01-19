package pl.librus.client.grades;

import android.support.annotation.NonNull;

import java.util.List;

import pl.librus.client.api.TextGrade;

/**
 * Created by szyme on 11.12.2016. librus-client
 */

public class TextGradeSummary extends GradeEntry {
    private String subjectId;
    private List<TextGrade> grades;
    private boolean isExpanded = false;

    TextGradeSummary(String subjectId, List<TextGrade> grades) {
        this.subjectId = subjectId;
        this.grades = grades;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    @Override
    public String getSubjectId() {
        return subjectId;
    }

    public List<TextGrade> getGrades() {
        return grades;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TextGradeSummary that = (TextGradeSummary) o;

        if (!subjectId.equals(that.subjectId)) return false;
        return grades != null ? grades.equals(that.grades) : that.grades == null;

    }

    @Override
    public int hashCode() {
        int result = subjectId.hashCode();
        result = 31 * result + (grades != null ? grades.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return -1;
    }
}
