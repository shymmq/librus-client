package pl.librus.client;

import pl.librus.client.ui.MainActivity;
import pl.librus.client.ui.SettingsFragment;
import pl.librus.client.ui.announcements.AnnouncementsFragment;
import pl.librus.client.ui.attendances.AttendanceFragment;
import pl.librus.client.ui.grades.GradesFragment;
import pl.librus.client.ui.timetable.TimetableFragment;

/**
 * Created by robwys on 28/03/2017.
 */
public interface BaseMainActivityComponent {
    void inject(MainActivity mainActivity);

    void inject(SettingsFragment fragment);

    void inject(TimetableFragment fragment);

    void inject(GradesFragment fragment);

    void inject(AnnouncementsFragment fragment);

    void inject(AttendanceFragment fragment);
}
