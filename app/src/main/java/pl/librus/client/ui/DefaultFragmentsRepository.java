package pl.librus.client.ui;

import com.google.common.collect.ImmutableList;

import java.util.List;

import pl.librus.client.announcements.AnnouncementsFragment;
import pl.librus.client.attendances.AttendanceFragment;
import pl.librus.client.grades.GradesFragment;
import pl.librus.client.timetable.TimetableFragment;

public abstract class DefaultFragmentsRepository {

    public List<MainFragment> getAll() {
        return ImmutableList.of(
                new TimetableFragment(),
                new GradesFragment(),
                new AnnouncementsFragment(),
                new AttendanceFragment()
        );
    }

}
