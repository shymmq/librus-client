package pl.librus.client.domain.lesson;

import com.google.common.collect.ImmutableList;

import org.joda.time.LocalDate;

import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Timetable extends HashMap<LocalDate, List<List<JsonLesson>>> {

    public List<Lesson> toLessons() {
        ImmutableList.Builder<Lesson> builder = ImmutableList.<Lesson>builder();

        for (Map.Entry<LocalDate, List<List<JsonLesson>>> e : entrySet()) {
            LocalDate date = e.getKey();
            for (List<JsonLesson> list : e.getValue()) {
                if (list.size() > 0) {
                    Lesson l = list.get(0).convert(date);
                    builder.add(l);
                }
            }
        }
        return builder.build();
    }

    public Timetable shallowCopy() {
        Timetable result = new Timetable();
        result.putAll(this);
        return result;
    }
}
