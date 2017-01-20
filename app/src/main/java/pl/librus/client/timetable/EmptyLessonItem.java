package pl.librus.client.timetable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.viewholders.FlexibleViewHolder;
import pl.librus.client.R;

/**
 * Created by szyme on 23.12.2016. librus-client
 */

class EmptyLessonItem extends AbstractSectionableItem<EmptyLessonItem.EmptyLessonItemViewHolder, LessonHeaderItem> implements Serializable {

    private static final long serialVersionUID = 8138475989480727823L;
    private final LocalDate date;

    EmptyLessonItem(LessonHeaderItem header, LocalDate date) {
        super(header);
        this.date = date;
    }

    @Override
    public EmptyLessonItemViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new EmptyLessonItemViewHolder(inflater.inflate(R.layout.lesson_empty_item, parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, EmptyLessonItemViewHolder holder, int position, List payloads) {
    }

    @Override
    public int getLayoutRes() {
        return R.layout.lesson_empty_item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmptyLessonItem that = (EmptyLessonItem) o;

        return date.equals(that.date);

    }

    @Override
    public int hashCode() {
        return date.hashCode();
    }

    class EmptyLessonItemViewHolder extends FlexibleViewHolder {

        EmptyLessonItemViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
        }
    }
}
