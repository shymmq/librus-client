package pl.librus.client.attendances;

import android.view.View;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.viewholders.FlexibleViewHolder;
import pl.librus.client.api.Attendance;

/**
 * Created by szyme on 28.01.2017.
 */
public class AttendanceItem extends AbstractSectionableItem<AttendanceItem.ViewHolder, AttendanceHeaderItem> {
    private Attendance attendance;

    public AttendanceItem(AttendanceHeaderItem header, Attendance attendance) {
        super(header);
        this.attendance = attendance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AttendanceItem that = (AttendanceItem) o;

        return attendance.equals(that.attendance);

    }

    @Override
    public int hashCode() {
        return attendance.hashCode();
    }

    class ViewHolder extends FlexibleViewHolder {

        public ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
        }
    }
}
