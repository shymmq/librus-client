package pl.librus.client.grades;

import android.support.annotation.NonNull;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.List;

import pl.librus.client.api.Grade;

/**
 * Created by szyme on 08.12.2016. librus-client
 */

class GradeCategory implements Parent<Grade>, Comparable {

    private List<Grade> grades;
    private String title;

    GradeCategory(List<Grade> grades, String title) {
        this.grades = grades;
        this.title = title;
    }

    @Override
    public List<Grade> getChildList() {
        return grades;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return title.compareTo(((GradeCategory) o).getTitle());
    }
}
