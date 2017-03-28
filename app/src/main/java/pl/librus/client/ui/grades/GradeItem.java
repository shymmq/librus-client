package pl.librus.client.ui.grades;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;
import pl.librus.client.R;
import pl.librus.client.data.Reader;
import pl.librus.client.domain.LibrusColor;
import pl.librus.client.domain.grade.EnrichedGrade;

class GradeItem extends AbstractFlexibleItem<GradeItem.ViewHolder> {
    private final EnrichedGrade grade;
    private final GradeHeaderItem header;

    GradeItem(GradeHeaderItem header, EnrichedGrade grade) {
        this.header = header;
        this.grade = grade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GradeItem gradeItem = (GradeItem) o;

        return grade.equals(gradeItem.grade);

    }

    @Override
    public int hashCode() {
        return grade.hashCode();
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {
        holder.grade.setText(grade.grade());
        holder.title.setText(grade.category().name());
        holder.subtitle.setText(grade.date().toString("EEEE, d MMMM", new Locale("pl")));
        Integer color = grade.category()
                .color()
                .transform(LibrusColor::colorInt)
                .or(Color.TRANSPARENT);
        holder.color.setBackgroundColor(color);
        holder.unreadBadge.setVisibility(
                new Reader(holder.itemView.getContext()).isRead(grade) ? View.GONE : View.VISIBLE);
    }

    @Override
    public ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.grade_item, parent, false), adapter);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.grade_item;
    }

    public EnrichedGrade getGrade() {
        return grade;
    }

    public GradeHeaderItem getHeader() {
        return header;
    }

    class ViewHolder extends FlexibleViewHolder {

        private final ImageView unreadBadge;
        private final TextView grade, title, subtitle;
        private final View color;

        ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            grade = (TextView) itemView.findViewById(R.id.grade_item_grade);
            title = (TextView) itemView.findViewById(R.id.grade_item_title);
            subtitle = (TextView) itemView.findViewById(R.id.grade_item_subtitle);
            unreadBadge = (ImageView) itemView.findViewById(R.id.grade_item_unread_badge);
            color = itemView.findViewById(R.id.grade_item_color);
        }
    }
}
