package pl.librus.client.attendances;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.LocalDate;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem;
import eu.davidea.viewholders.ExpandableViewHolder;
import pl.librus.client.R;

/**
 * Created by szyme on 28.01.2017.
 */

public class AttendanceHeaderItem extends AbstractExpandableHeaderItem<AttendanceHeaderItem.ViewHolder, AttendanceItem> {
    private LocalDate date;

    public AttendanceHeaderItem(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AttendanceHeaderItem that = (AttendanceHeaderItem) o;

        return date.equals(that.date);

    }

    @Override
    public int hashCode() {
        return date.hashCode();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.attendance_header_item;
    }

    @Override
    public ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {

    }

    class ViewHolder extends ExpandableViewHolder {

        public ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
        }
    }
}
