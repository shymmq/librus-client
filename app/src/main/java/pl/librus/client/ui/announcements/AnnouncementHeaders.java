package pl.librus.client.ui.announcements;

import android.content.Context;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import pl.librus.client.data.Reader;
import pl.librus.client.domain.announcement.BaseAnnouncement;

/**
 * Created by szyme on 31.12.2016.
 */

class AnnouncementHeaders {
    private final AnnouncementHeaderItem unread = new AnnouncementHeaderItem("Nieprzeczytane", 0);
    private final AnnouncementHeaderItem today = new AnnouncementHeaderItem("Dzisiaj", 1);
    private final AnnouncementHeaderItem yesterday = new AnnouncementHeaderItem("Wczoraj", 2);
    private final AnnouncementHeaderItem thisWeek = new AnnouncementHeaderItem("Ten tydzień", 3);
    private final AnnouncementHeaderItem thisMonth = new AnnouncementHeaderItem("Ten miesiąc", 4);
    private final AnnouncementHeaderItem older = new AnnouncementHeaderItem("Starsze", 5);
    private final Reader reader;

    AnnouncementHeaders(Context context) {
        this.reader = new Reader(context);
    }

    public AnnouncementHeaderItem getHeaderOf(BaseAnnouncement a) {
        LocalDate date = a.startDate();
        if (!reader.isRead(a))
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

}
