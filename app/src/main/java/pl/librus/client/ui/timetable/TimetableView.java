package pl.librus.client.ui.timetable;

import org.joda.time.LocalDate;

import java.util.List;

import pl.librus.client.domain.lesson.SchoolWeek;
import pl.librus.client.ui.MainView;

/**
 * Created by szyme on 04.04.2017.
 */

public interface TimetableView extends MainView<List<SchoolWeek>> {
    void setProgress(boolean enabled);

    void displayMore(SchoolWeek schoolWeek);

    void scrollToDay(LocalDate day);
}
