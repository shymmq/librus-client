package pl.librus.client;

import dagger.Subcomponent;
import pl.librus.client.ui.SettingsFragment;
import pl.librus.client.ui.announcements.AnnouncementsFragment;
import pl.librus.client.ui.attendances.AttendanceFragment;
import pl.librus.client.ui.grades.GradesFragment;
import pl.librus.client.ui.timetable.TimetableFragment;

/**
 * Created by robwys on 28/03/2017.
 */

@MainActivityScope
@Subcomponent(modules = {
        MainActivityModule.class,
        NotificationTesterModule.class
})
public interface MainActivityComponent extends BaseMainActivityComponent {

}

