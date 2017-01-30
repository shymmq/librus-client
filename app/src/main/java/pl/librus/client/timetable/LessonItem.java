package pl.librus.client.timetable;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.io.Serializable;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.viewholders.FlexibleViewHolder;
import pl.librus.client.R;
import pl.librus.client.api.Lesson;

/**
 * Created by szyme on 23.12.2016. librus-client
 */
//

class LessonItem extends AbstractSectionableItem<LessonItem.LessonItemViewHolder, LessonHeaderItem> implements Serializable {

    private static final long serialVersionUID = -7951416905429163498L;
    private transient final Context context;
    private Lesson lesson;

    LessonItem(LessonHeaderItem header, Lesson lesson, Context context) {
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
     * The unique instance of the LayoutInflater is also provided to simplify the
     * creation of the VH.
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

        holder.subject.setText(lesson.getSubject().getName());
        holder.teacher.setText(lesson.getTeacher().getName());
        holder.lessonNumber.setText(String.valueOf(lesson.getLessonNumber()));

        if (lesson.isCanceled()) {
            //lesson canceled
            holder.badge.setVisibility(View.VISIBLE);
            holder.badgeText.setText(R.string.canceled);
            holder.badgeIcon.setImageDrawable(context.getDrawable(R.drawable.ic_cancel_black_24dp));

        } else {

            if (lesson.isSubstitutionClass()) {
                //substitution
                holder.badge.setVisibility(View.VISIBLE);
                holder.badgeText.setText(R.string.substitution);
                holder.badgeIcon.setImageDrawable(context.getDrawable(R.drawable.ic_swap_horiz_black_24dp));
            } else {
                //normal lesson
                holder.badge.setVisibility(View.GONE);
            }

            LocalTime now = LocalTime.now();
            if (LocalDate.now().isEqual(lesson.getDate()) &&
                    now.isAfter(lesson.getStartTime()) &&
                    now.isBefore(lesson.getEndTime())) {
                holder.subject.setTypeface(holder.subject.getTypeface(), Typeface.BOLD);
            } else {
                holder.subject.setTypeface(null, Typeface.NORMAL);
            }
        }
    }

    /**
     * The LessonItemViewHolder used by this item.
     * Extending from FlexibleViewHolder is recommended especially when you will use
     * more advanced features.
     */
    class LessonItemViewHolder extends FlexibleViewHolder {
        final TextView subject, teacher, badgeText, lessonNumber;
        final ImageView badgeIcon;
        View badge;

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