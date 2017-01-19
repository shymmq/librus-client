package pl.librus.client.grades;

import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem;
import eu.davidea.flexibleadapter.items.AbstractExpandableItem;
import eu.davidea.flexibleadapter.items.IHeader;
import eu.davidea.flexibleadapter.items.ISectionable;
import eu.davidea.viewholders.ExpandableViewHolder;
import pl.librus.client.LibrusUtils;
import pl.librus.client.R;
import pl.librus.client.api.Subject;

/**
 * Created by szyme on 01.01.2017.
 */

class GradeSubjectItem extends AbstractExpandableHeaderItem<GradeSubjectItem.ViewHolder, ISectionable> {

    private static final String TAG = "librus-client-log";
    private Subject subject;
    private int gradeCount = 0;

    GradeSubjectItem(Subject subject) {
        super();
        this.subject = subject;
        setExpanded(false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GradeSubjectItem that = (GradeSubjectItem) o;

        return subject.equals(that.subject);

    }

    @Override
    public int hashCode() {
        return subject.hashCode();
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {
        holder.subject.setText(subject.getName());
        holder.gradeCount.setText(String.valueOf(gradeCount) + ' ' + LibrusUtils.getPluralForm(gradeCount, "ocena", "oceny", "ocen"));
    }

    @Override
    public ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.grade_category_item, parent, false), adapter);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.grade_category_item;
    }

    @Override
    public int getExpansionLevel() {
        return 0;
    }

    class ViewHolder extends ExpandableViewHolder {
        final private TextView subject, gradeCount;

        ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            gradeCount = (TextView) view.findViewById(R.id.grade_category_item_content);
            subject = (TextView) view.findViewById(R.id.grade_category_item_title);
        }
    }
}
