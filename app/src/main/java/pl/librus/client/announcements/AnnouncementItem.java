package pl.librus.client.announcements;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.Locale;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.viewholders.FlexibleViewHolder;
import pl.librus.client.R;
import pl.librus.client.datamodel.Announcement;
import pl.librus.client.datamodel.Teacher;
import pl.librus.client.ui.MainApplication;

/**
 * Created by szyme on 28.12.2016. librus-client
 */

class AnnouncementItem extends AbstractSectionableItem<AnnouncementItem.ViewHolder, AnnouncementHeaderItem> implements Comparable<AnnouncementItem> {
    private final Announcement announcement;
    private View backgroundView;
    private TextView title;
    private boolean read;

    public AnnouncementItem(Announcement announcement, AnnouncementHeaderItem header) {
        super(header);
        this.announcement = announcement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnnouncementItem)) return false;

        AnnouncementItem that = (AnnouncementItem) o;

        return announcement.equals(that.announcement);

    }

    public Announcement getAnnouncement() {
        return announcement;
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {
        this.backgroundView = holder.background;
        this.title = holder.announcementSubject;
        Teacher teacher = MainApplication.getData().findByKey(Teacher.class, announcement.addedBy().id());
        holder.announcementSubject.setText(announcement.subject());
        holder.background.setTransitionName("announcement_background_" + announcement.id());
        holder.announcementTeacherName.setText(teacher == null ? "" : teacher.name());
        holder.announcementContent.setText(announcement.content());

        if (!read)
            holder.announcementSubject.setTypeface(holder.announcementSubject.getTypeface(), Typeface.BOLD);
        else
            holder.announcementSubject.setTypeface(null, Typeface.NORMAL);
        if (announcement.startDate().isBefore(LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY)))
            holder.announcementDate.setText(announcement.startDate().toString("d MMM."));
        else
            holder.announcementDate.setText(announcement.startDate().dayOfWeek().getAsShortText(new Locale("pl")));
    }

    @Override
    public ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        View v = inflater.inflate(R.layout.three_line_list_item, parent, false);
        return new ViewHolder(v, adapter);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.three_line_list_item;
    }

    @Override
    public int hashCode() {
        return announcement.hashCode();
    }

    View getBackgroundView() {
        return backgroundView;
    }

    @Override
    public int compareTo(@NonNull AnnouncementItem o) {
        int a = Boolean.compare(read, o.isRead());
        if (a == 0)
            return o.getAnnouncement().startDate().compareTo(announcement.startDate());
        else return a;
    }

    private boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public TextView getTitle() {
        return title;
    }

    class ViewHolder extends FlexibleViewHolder {
        public final RelativeLayout background;
        final TextView announcementTeacherName,
                announcementSubject,
                announcementContent,
                announcementDate;

        ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            background = (RelativeLayout) view.findViewById(R.id.three_line_list_item_background);
            announcementSubject = (TextView) view.findViewById(R.id.three_line_list_item_title);
            announcementTeacherName = (TextView) view.findViewById(R.id.three_line_list_item_first);
            announcementContent = (TextView) view.findViewById(R.id.three_line_list_item_second);
            announcementDate = (TextView) view.findViewById(R.id.three_line_list_item_date);
        }
    }
}
