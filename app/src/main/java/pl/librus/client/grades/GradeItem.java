package pl.librus.client.grades;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.viewholders.FlexibleViewHolder;
import pl.librus.client.R;
import pl.librus.client.api.Grade;
import pl.librus.client.api.GradeComment;
import pl.librus.client.api.LibrusData;

/**
 * Created by szyme on 01.01.2017.
 */

class GradeItem extends AbstractSectionableItem<GradeItem.ViewHolder, GradeSubjectItem> {
    private final Grade grade;
    private final LibrusData data;

    GradeItem(GradeSubjectItem header, Grade grade, LibrusData data) {
        super(header);
        this.grade = grade;
        this.data = data;
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
        holder.grade.setText(grade.getGrade());
        holder.title.setText(data.getGradeCategoriesMap().get(grade.getCategoryId()).getName());
        holder.subtitle.setText(grade.getDate().toString("EEEE, d MMMM", new Locale("pl")));
        holder.commentBadge.setVisibility(grade.getCommentId() == null ? View.GONE : View.VISIBLE);
    }

    @Override
    public ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.grade_item, parent, false), adapter);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.grade_item;
    }


    class ViewHolder extends FlexibleViewHolder {

        private final ImageView commentBadge;
        private final TextView grade, title, subtitle;

        ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            grade = (TextView) itemView.findViewById(R.id.grade_item_grade);
            title = (TextView) itemView.findViewById(R.id.grade_item_title);
            subtitle = (TextView) itemView.findViewById(R.id.grade_item_subtitle);
            commentBadge = (ImageView) itemView.findViewById(R.id.grade_item_comment_badge);
        }
    }
}
