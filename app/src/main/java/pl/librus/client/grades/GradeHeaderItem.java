package pl.librus.client.grades;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.collect.FluentIterable;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.Payload;
import eu.davidea.flexibleadapter.items.AbstractHeaderItem;
import eu.davidea.flexibleadapter.items.IExpandable;
import eu.davidea.viewholders.ExpandableViewHolder;
import pl.librus.client.LibrusUtils;
import pl.librus.client.R;
import pl.librus.client.api.Reader;
import pl.librus.client.datamodel.Average;
import pl.librus.client.datamodel.Subject;

/**
 * Created by szyme on 01.01.2017.
 * Header item for grades fragment
 */

@SuppressWarnings("Guava")
class GradeHeaderItem
        extends AbstractHeaderItem<GradeHeaderItem.ViewHolder>
        implements IExpandable<GradeHeaderItem.ViewHolder, GradeItem>, Comparable<GradeHeaderItem> {

    private final Subject subject;
    private final Average average;
    private final Reader reader;
    private Context context;
    private boolean expanded;
    private ArrayList<GradeItem> mSubItems;

    GradeHeaderItem(Subject subject, Average average, Context context) {
        super();
        this.subject = subject;
        this.average = average;
        this.reader = new Reader(context);
        this.context = context;
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
    public void bindViewHolder(FlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {
        boolean expanded = payloads.contains(Payload.EXPANDED);

//        if (expanded &&
//                getSubItems() != null &&
//                !getSubItems().isEmpty()) {
//            adapter.expand(getSubItems().getAll(0));
//        }

        //getAll grade count and unread grade count

        int gradeCount = getGradeCount();
        int unreadGradeCount = getUnreadGradeCount();

        setEnabled(gradeCount > 0);
        holder.subject.setText(subject.name());
        holder.averageSummary.setVisibility(expanded ? View.VISIBLE : View.GONE);
        holder.gradeCountView.setVisibility(expanded ? View.GONE : View.VISIBLE);
        holder.background.setAlpha(gradeCount > 0 ? 1f : 0.5f);
        holder.arrow.animate().rotation(expanded ? 180f : 0f).start();

        SpannableStringBuilder ssb = new SpannableStringBuilder();
        if (gradeCount == 0) {
            ssb.append(context.getString(R.string.no_grades));
        } else {
            ssb.append(String.valueOf(gradeCount))
                    .append(' ')
                    .append(LibrusUtils.getPluralForm(gradeCount, "ocena", "oceny", "ocen"));
            if (unreadGradeCount > 0) {
                ssb.append("  ")
                        .append(String.valueOf(unreadGradeCount),
                                new ForegroundColorSpan(Color.parseColor("#FF5722")),
                                Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                        .append(' ')
                        .append(LibrusUtils.getPluralForm(unreadGradeCount, "nowa", "nowe", "nowych"),
                                new ForegroundColorSpan(Color.parseColor("#FF5722")),
                                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
        holder.gradeCountView.setText(ssb);

        if (average != null) {
            String s = holder.itemView.getContext().getString(R.string.average_);
            Spannable averageSummaryText = new SpannableString(s + average.fullYear());
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
    public boolean isExpanded() {
        return expanded;
    }

    @Override
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    void addSubItem(GradeItem subItem) {
        if (mSubItems == null)
            mSubItems = new ArrayList<>();
        mSubItems.add(subItem);
    }

    @Override
    public int getExpansionLevel() {
        return 0;
    }

    @Override
    public List<GradeItem> getSubItems() {
        return mSubItems;
    }

    @Override
    public int compareTo(@NonNull GradeHeaderItem o) {
        int countCompare = Boolean.compare(o.getGradeCount() > 0, getGradeCount() > 0);
        if (countCompare != 0) return countCompare;
        else return subject.name().compareTo(o.getSubject().name());
    }

    public Subject getSubject() {
        return subject;
    }

    private int getGradeCount() {
        if (getSubItems() == null) return 0;
        return FluentIterable.from(getSubItems())
                .filter(GradeItem.class)
                .size();
    }

    private int getUnreadGradeCount() {
        if (getSubItems() == null) return 0;
        return FluentIterable.from(getSubItems())
                .filter(GradeItem.class)
                .filter(input -> !reader.isRead(input.getGrade()))
                .size();
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
