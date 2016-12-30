package pl.librus.client.announcements;

import android.graphics.Typeface;
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
import pl.librus.client.api.Announcement;
import pl.librus.client.api.LibrusData;
import pl.librus.client.api.Reader;
import pl.librus.client.api.Teacher;

/**
 * Created by szyme on 28.12.2016. librus-client
 */

class AnnouncementItem extends AbstractSectionableItem<AnnouncementItem.ViewHolder, AnnouncementHeaderItem> {
    private final LibrusData data;
    private Announcement announcement;
    private AnnouncementHeaderItem header;
    private View backgroundView;

    public AnnouncementItem(Announcement announcement, LibrusData data, AnnouncementHeaderItem header) {
        super(header);
        this.announcement = announcement;
        this.data = data;
        this.header = header;
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
        Teacher teacher = data.getTeacherMap().get(announcement.getAuthorId());
        holder.announcementSubject.setText(announcement.getSubject());
        holder.background.setTransitionName("announcement_background_" + announcement.getId());
        holder.announcementTeacherName.setText(teacher.getName());
        holder.announcementContent.setText(announcement.getContent());

        if (!Reader.isRead(Reader.TYPE_ANNOUNCEMENT, announcement.getId(), data.getContext()))
            holder.announcementSubject.setTypeface(holder.announcementSubject.getTypeface(), Typeface.BOLD);
        else
            holder.announcementSubject.setTypeface(holder.announcementSubject.getTypeface(), Typeface.NORMAL);

        if (announcement.getStartDate().isBefore(LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY)))
            holder.announcementDate.setText(announcement.getStartDate().toString("d MMM."));
        else
            holder.announcementDate.setText(announcement.getStartDate().dayOfWeek().getAsShortText(new Locale("pl")));
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

    public View getBackgroundView() {
        return backgroundView;
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
