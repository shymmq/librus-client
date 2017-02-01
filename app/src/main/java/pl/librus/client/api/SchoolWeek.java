package pl.librus.client.api;

import android.support.annotation.NonNull;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class SchoolWeek implements Comparable<SchoolWeek> {

    private final List<SchoolDay> schoolDays = new ArrayList<>();
    private final LocalDate weekStart;

    SchoolWeek(LocalDate weekStart) {
        this.weekStart = weekStart;
    }

    void addSchoolDay(SchoolDay schoolDay) {
        schoolDays.add(schoolDay);
    }

    public List<SchoolDay> getSchoolDays() {
        return schoolDays;
    }

    public LocalDate getWeekStart() {
        return weekStart;
    }

    @Override
    public int compareTo(@NonNull SchoolWeek week) {
        return weekStart.compareTo(week.getWeekStart());
    }
}
