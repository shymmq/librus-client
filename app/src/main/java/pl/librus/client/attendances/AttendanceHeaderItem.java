package pl.librus.client.attendances;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.LocalDate;

import java.util.List;
import java.util.Locale;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem;
import eu.davidea.viewholders.ExpandableViewHolder;
import pl.librus.client.R;

/**
 * Created by szyme on 28.01.2017.
 */

class AttendanceHeaderItem extends AbstractExpandableHeaderItem<AttendanceHeaderItem.ViewHolder, AttendanceItem> {
    private LocalDate date;

    AttendanceHeaderItem(LocalDate date) {
        setHidden(false);
        setExpanded(false);
        //NOT selectable, otherwise (if you have) the ActionMode will be activated on long click!
        setSelectable(false);
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
    public int getExpansionLevel() {
        return 0;
    }

    @Override
    public ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {
        holder.date.setText(date.toString(holder.itemView.getContext().getString(R.string.date_format_no_year), new Locale("pl")));
        holder.content.setText(String.valueOf(getSubItemsCount()));
    }

    class ViewHolder extends ExpandableViewHolder {
        private final TextView date, content;

        public ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            content = (TextView) view.findViewById(R.id.attendance_header_item_content);
            date = (TextView) view.findViewById(R.id.attendance_header_item_title);
        }
    }
}
