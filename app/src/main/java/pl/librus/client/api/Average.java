package pl.librus.client.api;

import android.support.annotation.NonNull;

import java.io.Serializable;

import pl.librus.client.grades.GradeEntry;

/**
 * Created by szyme on 08.12.2016. librus-client
 */

public class Average extends GradeEntry implements Serializable {
    private static final long serialVersionUID = 6054144699190921436L;
    private double semester1, semester2, fullYear;
    private String subjectId;

    Average(String subjectId, double semester1, double semester2, double fullYear) {
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
        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Average average = (Average) o;

        return Double.compare(average.semester1, semester1) == 0 &&
                Double.compare(average.semester2, semester2) == 0 &&
                Double.compare(average.fullYear, fullYear) == 0 &&
                subjectId.equals(average.subjectId);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(semester1);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(semester2);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(fullYear);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + subjectId.hashCode();
        return result;
    }
}
