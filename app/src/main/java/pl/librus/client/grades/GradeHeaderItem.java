package pl.librus.client.grades;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.Payload;
import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem;
import eu.davidea.flexibleadapter.items.ISectionable;
import eu.davidea.viewholders.ExpandableViewHolder;
import pl.librus.client.LibrusUtils;
import pl.librus.client.R;
import pl.librus.client.api.Average;
import pl.librus.client.datamodel.Subject;

/**
 * Created by szyme on 01.01.2017.
 */

class GradeHeaderItem extends AbstractExpandableHeaderItem<GradeHeaderItem.ViewHolder, ISectionable> implements Comparable<GradeHeaderItem> {

    private static final String TAG = "librus-client-log";
    private Subject subject;
    private final Average average;
    private int gradeCount = 0;

    GradeHeaderItem(Subject subject, Average average) {
        super();
        this.subject = subject;
        this.average = average;
        setExpanded(false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GradeHeaderItem that = (GradeHeaderItem) o;

        return subject.equals(that.subject);

    }

    @Override
    public int hashCode() {
        return subject.hashCode();
    }

    @Override
    public void addSubItem(ISectionable subItem) {
        super.addSubItem(subItem);
        if (subItem instanceof GradeItem)
            gradeCount++;
    }

    GradeHeaderItem sort() {
//        if (mSubItems != null) Collections.sort(mSubItems, Collections.reverseOrder());
        return this;
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {
        setEnabled(gradeCount > 0);
        boolean expanded = payloads.contains(Payload.EXPANDED);
        holder.subject.setText(subject.getName());
        holder.averageSummary.setVisibility(expanded ? View.VISIBLE : View.GONE);
        holder.gradeCountView.setVisibility(expanded ? View.GONE : View.VISIBLE);
        holder.background.setAlpha(gradeCount > 0 ? 1f : 0.5f);
        holder.arrow.animate().rotation(expanded ? 180f : 0f).start();
        holder.gradeCountView.setText("Brak ocen");
        holder.gradeCountView.setText(String.valueOf(gradeCount) + ' ' + LibrusUtils.getPluralForm(gradeCount, "ocena", "oceny", "ocen"));

        if (average != null) {
            String s = "Średnia: ";
            Spannable averageSummaryText = new SpannableString(s + average.getFullYear());
            averageSummaryText.setSpan(new StyleSpan(Typeface.BOLD), s.length(), averageSummaryText.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            holder.averageSummary.setText(averageSummaryText);
        }
    }

    @Override
    public ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.grade_header_item, parent, false), adapter);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.grade_header_item;
    }

    @Override
    public int getExpansionLevel() {
        return 0;
    }

    @Override
    public int compareTo(@NonNull GradeHeaderItem o) {
        int countCompare = Boolean.compare(o.gradeCount > 0, gradeCount > 0);
        if (countCompare != 0) return countCompare;
        else return subject.getName().compareTo(o.getSubject().getName());
    }

    public Subject getSubject() {
        return subject;
    }

    class ViewHolder extends ExpandableViewHolder {
        final private TextView subject, gradeCountView, averageSummary;
        final private View background, arrow;

        ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            background = view.findViewById(R.id.grade_category_item_root);
            arrow = view.findViewById(R.id.grade_header_item_arrow);
            gradeCountView = (TextView) view.findViewById(R.id.grade_header_item_content_1);
            averageSummary = (TextView) view.findViewById(R.id.grade_header_item_content_2);
            subject = (TextView) view.findViewById(R.id.grade_header_item_title);
        }

        @Override
        protected boolean shouldNotifyParentOnClick() {
            return true;
        }
    }
}
