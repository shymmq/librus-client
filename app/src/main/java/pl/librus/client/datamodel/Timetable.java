package pl.librus.client.datamodel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.librus.client.ui.MainApplication;

/**
 * Created by robwys on 04/02/2017.
 */

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
}
