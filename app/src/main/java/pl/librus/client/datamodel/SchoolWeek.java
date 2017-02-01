package pl.librus.client.datamodel;

import android.support.annotation.NonNull;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SchoolWeek implements Comparable<SchoolWeek> {

    private final List<SchoolDay> schoolDays = new ArrayList<>();

    public void addSchoolDay(SchoolDay schoolDay) {
        schoolDays.add(schoolDay);
    }

    public List<SchoolDay> getSchoolDays() {
        return schoolDays;
    }

    public LocalDate getWeekStart() {
        return Collections.min(schoolDays).getDate();
    }

    @Override
    public int compareTo(@NonNull SchoolWeek week) {
        return getWeekStart().compareTo(week.getWeekStart());
    }
}
