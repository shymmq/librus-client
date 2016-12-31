package pl.librus.client.announcements;

import android.content.Context;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.Comparator;

import pl.librus.client.api.Announcement;
import pl.librus.client.api.Reader;

/**
 * Created by szyme on 31.12.2016.
 */

class AnnouncementUtils {
    private static AnnouncementHeaderItem unread = new AnnouncementHeaderItem("Nieprzeczytane", 0, true);
    private static AnnouncementHeaderItem today = new AnnouncementHeaderItem("Dzisiaj", 1);
    private static AnnouncementHeaderItem yesterday = new AnnouncementHeaderItem("Wczoraj", 2);
    private static AnnouncementHeaderItem thisWeek = new AnnouncementHeaderItem("Ten tydzień", 3);
    private static AnnouncementHeaderItem thisMonth = new AnnouncementHeaderItem("Ten miesiąc", 4);
    private static AnnouncementHeaderItem older = new AnnouncementHeaderItem("Starsze", 5);

    static AnnouncementHeaderItem getHeaderOf(Announcement a, Context c) {
        LocalDate date = a.getStartDate();
        if (!Reader.isRead(Reader.TYPE_ANNOUNCEMENT, a.getId(), c))
            return unread;
        else if (!date.isBefore(LocalDate.now()))
            return today;
        else if (!date.isBefore(LocalDate.now().minusDays(1)))
            return yesterday;
        else if (!date.isBefore(LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY)))
            return thisWeek;
        else if (!date.isBefore(LocalDate.now().withDayOfMonth(1)))
            return thisMonth;
        else
            return older;
    }

    static Comparator getItemComparator() {
        return new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof AnnouncementItem && o2 instanceof AnnouncementItem)
                    return ((AnnouncementItem) o1).getAnnouncement().getStartDate().compareTo(((AnnouncementItem) o2).getAnnouncement().getStartDate());
                else return 0;
            }
        };
    }
}
