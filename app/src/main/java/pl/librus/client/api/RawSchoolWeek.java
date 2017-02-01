package pl.librus.client.api;

import org.joda.time.LocalDate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.librus.client.datamodel.Lesson;

/**
 * Created by szyme on 01.02.2017.
 */

public class RawSchoolWeek extends HashMap<LocalDate, List<List<Lesson>>> {
    SchoolWeek toSchoolWeek() {
        SchoolWeek result = new SchoolWeek(getWeekStart());
        for (Map.Entry<LocalDate, List<List<Lesson>>> entry : entrySet()) {
            SchoolDay schoolDay = new SchoolDay(entry.getKey());
            for (List<Lesson> lessons : entry.getValue()) {
                if (!lessons.isEmpty()) {
                    Lesson lesson = lessons.get(0);
                    schoolDay.setLesson(lesson.getLessonNumber(), lesson);
                }
            }
        }
        return result;
    }

    private LocalDate getWeekStart() {
        return Collections.min(keySet());
    }
}
