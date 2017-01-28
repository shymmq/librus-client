package pl.librus.client.attendances;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.viewholders.FlexibleViewHolder;
import pl.librus.client.R;
import pl.librus.client.api.Attendance;

class AttendanceItem extends AbstractSectionableItem<AttendanceItem.ViewHolder, AttendanceHeaderItem> {
    private Attendance attendance;

    AttendanceItem(AttendanceHeaderItem header, Attendance attendance) {
        super(header);
        this.attendance = attendance;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.attendance_item;
    }

    @Override
    public ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {

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
