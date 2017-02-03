package pl.librus.client.timetable;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractHeaderItem;
import eu.davidea.viewholders.FlexibleViewHolder;
import pl.librus.client.R;

/**
 * Created by szyme on 23.12.2016. librus-client
 */

class LessonHeaderItem extends AbstractHeaderItem<LessonHeaderItem.LessonHeaderItemViewHolder> implements Serializable {
    private static final long serialVersionUID = -7280842585284962070L;
    private LocalDate date;

    LessonHeaderItem(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LessonHeaderItem that = (LessonHeaderItem) o;

        return date.equals(that.date);
    }

    @Override
    public int hashCode() {
        return date.hashCode();
    }

    @Override
    public LessonHeaderItemViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new LessonHeaderItemViewHolder(inflater.inflate(R.layout.list_subheader, parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, LessonHeaderItemViewHolder holder, int position, List payloads) {
//        holder.done.setVisibility(View.GONE);
        String title;
        String subtitle = date.toString("d.M");
        //determine if day is today or tomorrow
        if (date.equals(LocalDate.now())) {
            title = "Dzisiaj";
        } else if (date.equals(LocalDate.now().plusDays(1))) {
            title = "Jutro";
        } else {
            title = date.toString("EEEE", new Locale("pl"));
        }
        //Set title to bold
        SpannableString sectionText = new SpannableString(title.substring(0, 1).toUpperCase() + title.substring(1) + ' ' + subtitle);
        sectionText.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.title.setText(sectionText);
    }

    public LocalDate getDate() {
        return date;
    }

    class LessonHeaderItemViewHolder extends FlexibleViewHolder {
        TextView title;
//        View done;

        LessonHeaderItemViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            this.title = (TextView) view.findViewById(R.id.list_subheader_title);
//            this.done = view.findViewById(R.id.list_subheader_done);
        }
    }
}
