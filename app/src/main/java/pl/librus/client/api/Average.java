package pl.librus.client.api;

import android.support.annotation.NonNull;

import java.io.Serializable;

import pl.librus.client.grades.GradeEntry;

/**
 * Created by szyme on 08.12.2016. librus-client
 */

public class Average extends GradeEntry implements Serializable {
    private static final long serialVersionUID = 6054144699190921436L;
    double semester1, semester2, fullYear;
    String subjectId;

    public Average(String subjectId, double semester1, double semester2, double fullYear) {
        this.semester1 = semester1;
        this.semester2 = semester2;
        this.fullYear = fullYear;
        this.subjectId = subjectId;
    }

    public double getSemester1() {
        return semester1;
    }

    public double getSemester2() {
        return semester2;
    }

    public double getFullYear() {
        return fullYear;
    }

    public String getSubjectId() {
        return subjectId;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return o instanceof Grade ? 1 : 0;
    }
}
