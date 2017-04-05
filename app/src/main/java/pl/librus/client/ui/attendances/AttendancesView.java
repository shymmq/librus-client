package pl.librus.client.ui.attendances;

import java.util.List;

import pl.librus.client.domain.attendance.FullAttendance;
import pl.librus.client.ui.MainView;

/**
 * Created by szyme on 04.04.2017.
 */

public interface AttendancesView extends MainView<List<FullAttendance>> {
    void displayPopup(FullAttendance fullAttendance);

}
