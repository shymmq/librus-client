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
    public int compareTo(@NonNull Object o) {
        return -1;
    }
}
