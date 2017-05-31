package pl.librus.client.ui.timetable;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.viewholders.FlexibleViewHolder;
import pl.librus.client.R;
import pl.librus.client.domain.lesson.EnrichedLesson;
import pl.librus.client.util.LibrusUtils;

/**
 * Created by szyme on 23.12.2016. librus-client
 */
//

public class LessonItem extends AbstractSectionableItem<LessonItem.LessonItemViewHolder, LessonHeaderItem> {

    private transient final Context context;
    private final EnrichedLesson lesson;

    public LessonItem(LessonHeaderItem header, EnrichedLesson lesson, Context context) {
        super(header);
        this.context = context;
        this.lesson = lesson;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LessonItem that = (LessonItem) o;

        return lesson.equals(that.lesson);

    }

    @Override
    public int hashCode() {
        return lesson.hashCode();
    }

    /**
     * For the item type we need an int value: the layoutResID is sufficient.
     */
    @Override
    public int getLayoutRes() {
        return R.layout.lesson_item;
    }

    /**
     * The Adapter is provided to be forwarded to the MyViewHolder.
     * The unique instance create the LayoutInflater is also provided to simplify the
     * creation create the VH.
     */
    @Override
    public LessonItemViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater,
                                                 ViewGroup parent) {
        return new LessonItemViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    /**
     * The Adapter and the Payload are provided to get more specific information from it.
     */
    @Override
    public void bindViewHolder(FlexibleAdapter adapter, LessonItemViewHolder holder, int position,
                               List payloads) {
        holder.subject.setText(lesson.subject().name());
        LibrusUtils.setTextViewValue(holder.teacher, lesson.teacher().name());
        holder.lessonNumber.setText(String.valueOf(lesson.lessonNo()));

        if (lesson.cancelled()) {
            //cancelled
            holder.badge.setVisibility(View.VISIBLE);
            holder.badgeText.setText(R.string.canceled);
            Drawable cancelledIcon = ContextCompat.getDrawable(context, R.drawable.ic_cancel_black_24dp);
            holder.badgeIcon.setImageDrawable(cancelledIcon);
        } else if (lesson.event().isPresent()) {
            //event
            holder.badge.setVisibility(View.VISIBLE);
            String categoryName = lesson.event().get().category().name();
            holder.badgeText.setText(categoryName);
            Drawable eventIcon = ContextCompat.getDrawable(context, R.drawable.ic_event_black_24dp);
            holder.badgeIcon.setImageDrawable(eventIcon);
        } else if (lesson.substitutionClass()) {
            //substitution
            holder.badge.setVisibility(View.VISIBLE);
            holder.badgeText.setText(R.string.substitution);
            Drawable substitutionIcon = ContextCompat.getDrawable(context, R.drawable.ic_swap_horiz_black_24dp);
            holder.badgeIcon.setImageDrawable(substitutionIcon);
        } else {
            //normal lesson
            holder.badge.setVisibility(View.GONE);
        }

        if (lesson.current()) {
            holder.subject.setTypeface(holder.subject.getTypeface(), Typeface.BOLD);
        } else {
            holder.subject.setTypeface(null, Typeface.NORMAL);
        }
    }

    public EnrichedLesson getLesson() {
        return lesson;
    }

    /**
     * The LessonItemViewHolder used by this item.
     * Extending from FlexibleViewHolder is recommended especially when you will use
     * more advanced features.
     */
    class LessonItemViewHolder extends FlexibleViewHolder {
        final TextView subject, teacher, badgeText, lessonNumber;
        final ImageView badgeIcon;
        final View badge;

        LessonItemViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            subject = (TextView) itemView.findViewById(R.id.lessonSubject);
            teacher = (TextView) itemView.findViewById(R.id.lessonTeacher);
            badge = itemView.findViewById(R.id.badge);
            badgeIcon = (ImageView) itemView.findViewById(R.id.badgeIcon);
            badgeText = (TextView) itemView.findViewById(R.id.badgeText);
            lessonNumber = (TextView) itemView.findViewById(R.id.lessonNumber);
        }
    }
}