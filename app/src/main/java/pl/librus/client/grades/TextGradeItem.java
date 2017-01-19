package pl.librus.client.grades;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.viewholders.FlexibleViewHolder;
import pl.librus.client.R;
import pl.librus.client.api.TextGrade;

/**
 * Created by szyme on 01.01.2017.
 */
class TextGradeItem extends AbstractFlexibleItem<TextGradeItem.ViewHolder> {
    private TextGrade textGrade;

    public TextGradeItem(TextGrade textGrade) {
        this.textGrade = textGrade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TextGradeItem that = (TextGradeItem) o;

        return textGrade.equals(that.textGrade);

    }

    @Override
    public int hashCode() {
        return textGrade.hashCode();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.text_grade_item;
    }

    @Override
    public ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.text_grade_item, parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {

    }

    class ViewHolder extends FlexibleViewHolder {

        ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
        }
    }
}
