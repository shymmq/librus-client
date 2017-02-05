package pl.librus.client.timetable;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import pl.librus.client.datamodel.Lesson;

public class SchoolWeek {

    private final List<SchoolDay> schoolDays = new ArrayList<>(7);

    public SchoolWeek(LocalDate weekStart, List<Lesson> lessons) {
        for(int i = 0; i<7; i++) {
            schoolDays.add(new SchoolDay(weekStart.plusDays(i)));
        }
        for(Lesson l : lessons) {
            addLesson(l);
        }
    }

    public void addLesson(Lesson lesson) {
        schoolDays.get(lesson.dayNo()-1).addLesson(lesson);
    }

    public List<SchoolDay> getSchoolDays() {
        return schoolDays;
    }

}
