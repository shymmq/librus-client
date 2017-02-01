package pl.librus.client.api;

import org.joda.time.LocalDate;

import java.util.HashMap;
import java.util.List;

import pl.librus.client.datamodel.Lesson;

/**
 * Created by szyme on 30.01.2017.
 */

public class Timetable extends HashMap<LocalDate, List<List<Lesson>>> {
    private static final long serialVersionUID = -7541646374727962937L;
}
