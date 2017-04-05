package pl.librus.client.ui.announcements;

import java.util.List;

import pl.librus.client.domain.announcement.FullAnnouncement;
import pl.librus.client.ui.MainView;

/**
 * Created by szyme on 04.04.2017.
 */

public interface AnnouncementsView extends MainView<List<? extends FullAnnouncement>> {
    void displayDetails(AnnouncementItem announcementItem);

    void setRefreshing(boolean b);
}
