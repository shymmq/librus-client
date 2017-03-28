package pl.librus.client.ui.grades;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.viewholders.FlexibleViewHolder;
import pl.librus.client.R;
import pl.librus.client.domain.Average;

/**
 * Created by szyme on 01.01.2017.
 * Subitem for grades fragment
 */

class AverageItem extends AbstractSectionableItem<AverageItem.ViewHolder, GradeHeaderItem> {
    private final Average average;

    AverageItem(GradeHeaderItem header, Average average) {
        super(header);
        this.average = average;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AverageItem that = (AverageItem) o;

        return average.equals(that.average);

    }

    @Override
    public int hashCode() {
        return average.hashCode();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.average_item;
    }

    @Override
    public AverageItem.ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.average_item, parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {
        holder.average.setText(String.valueOf(average.fullYear()));
    }

    class ViewHolder extends FlexibleViewHolder {

        private final TextView average;

        ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            average = (TextView) itemView.findViewById(R.id.average_item_average);
        }
    }
}
