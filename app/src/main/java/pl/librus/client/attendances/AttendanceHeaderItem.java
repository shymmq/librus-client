package pl.librus.client.attendances;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.LocalDate;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem;
import eu.davidea.viewholders.ExpandableViewHolder;
import pl.librus.client.LibrusUtils;
import pl.librus.client.R;

/**
 * Created by szyme on 28.01.2017.
 * Header item for FlexibleAdapter
 */

class AttendanceHeaderItem extends AbstractExpandableHeaderItem<AttendanceHeaderItem.ViewHolder, AttendanceItem> implements Comparable<AttendanceHeaderItem> {
    private final LocalDate date;
    private int nbCount = 0;
    private int spCount = 0;

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
    public void addSubItem(AttendanceItem subItem) {
        super.addSubItem(subItem);
        if (Objects.equals(subItem.getCategory().getShortName(), "sp")) spCount++;
        if (Objects.equals(subItem.getCategory().getShortName(), "nb")) nbCount++;
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {
        holder.date.setText(date.toString(holder.itemView.getContext().getString(R.string.date_format_no_year), new Locale("pl")));
        String spSummary = spCount > 0 ? (spCount + " " + LibrusUtils.getPluralForm(spCount, "spóźnienie", "spóźnienia", "spóźnień")) : "";
        String nbSummary = nbCount > 0 ? (nbCount + " " + LibrusUtils.getPluralForm(nbCount, "nieobecność", "nieobecności", "nieobecności")) : "";
        holder.content.setText(nbSummary +
                ((nbCount > 0 && spCount > 0) ? ", " : "") +
                spSummary);
    }

    @Override
    public int compareTo(@NonNull AttendanceHeaderItem o) {
        return date.compareTo(o.date);
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
