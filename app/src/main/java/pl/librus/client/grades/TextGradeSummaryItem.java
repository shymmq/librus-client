package pl.librus.client.grades;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem;
import eu.davidea.flexibleadapter.items.AbstractExpandableItem;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IExpandable;
import eu.davidea.flexibleadapter.items.IHeader;
import eu.davidea.flexibleadapter.items.ISectionable;
import eu.davidea.viewholders.ExpandableViewHolder;
import pl.librus.client.R;
import pl.librus.client.api.TextGrade;

/**
 * Created by szyme on 01.01.2017.
 */

public class TextGradeSummaryItem extends AbstractExpandableItem<TextGradeSummaryItem.ViewHolder, TextGradeItem> implements ISectionable<TextGradeSummaryItem.ViewHolder, GradeSubjectItem> {
    private GradeSubjectItem header;

    public TextGradeSummaryItem(GradeSubjectItem header) {
        this.header = header;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.text_grade_summary_item;
    }

    @Override
    public ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.text_grade_summary_item, parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TextGradeSummaryItem that = (TextGradeSummaryItem) o;

        return header.equals(that.header);

    }

    @Override
    public int hashCode() {
        return header.hashCode();
    }

    @Override
    public GradeSubjectItem getHeader() {
        return header;
    }

    @Override
    public int getExpansionLevel() {
        return 1;
    }

    @Override
    public void setHeader(GradeSubjectItem header) {
        this.header = header;
    }

    class ViewHolder extends ExpandableViewHolder {

        public ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
        }
    }
}
