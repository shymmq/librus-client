package pl.librus.client.api;

import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SchoolWeek implements Serializable {

    private static final long serialVersionUID = -1448021508657217605L;
    private final String TAG = "librus-client-log";
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

    LocalDate getWeekStart() {
        return weekStart;
    }
}
