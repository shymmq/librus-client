package pl.librus.client.timetable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.viewholders.FlexibleViewHolder;
import pl.librus.client.R;

/**
 * Created by szyme on 10.03.2017.
 */

public class MissingLessonItem extends AbstractSectionableItem<MissingLessonItem.ViewHolder, LessonHeaderItem> {

    private final LessonHeaderItem header;
    private final int lessonNo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MissingLessonItem that = (MissingLessonItem) o;

        if (lessonNo != that.lessonNo) return false;
        return header.equals(that.header);

    }

    @Override
    public int hashCode() {
        int result = header.hashCode();
        result = 31 * result + lessonNo;
        return result;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.missing_lesson_item;
    }

    @Override
    public ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {
        holder.lessonNo.setText(String.valueOf(lessonNo));
    }

    public MissingLessonItem(LessonHeaderItem header, int lessonNo) {
        super(header);
        this.header = header;
        this.lessonNo = lessonNo;
    }

    class ViewHolder extends FlexibleViewHolder {
        private final TextView lessonNo;

        public ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);

            lessonNo = (TextView) view.findViewById(R.id.missing_lesson_item_lesson_number);
        }
    }
}
