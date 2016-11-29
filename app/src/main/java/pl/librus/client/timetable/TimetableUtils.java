package pl.librus.client.timetable;

import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.Locale;

public class TimetableUtils {

    //returns default show tab index
    public static int getStartTab() {
        int weekday = LocalDate.now().getDayOfWeek();
        if (weekday >= DateTimeConstants.SATURDAY) {
            return weekday - 1 + 5;
        } else {
            return weekday - 1;
        }
    }


    //returns first week for which to start downloading
    public static LocalDate getWeekStart() {
        return LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY);
    }

    public static int getDayCount() {
//        return Days.daysBetween(getStartDate(), getWeekStart().plusWeeks(2)).getDays() - 4;
        return 10;
    }

    public static String getTabTitle(int index, boolean displayDates, boolean useRelativeTabNames) {
        return getTitle(getTabDate(index), displayDates, useRelativeTabNames);
    }

    //returns index for given date
    public static LocalDate getTabDate(int index) {
        LocalDate weekStart = getWeekStart();
        if (index >= 5) {
            return weekStart.plusDays(index + 2);
        }
        return weekStart.plusDays(index);
    }

    //return title String for given date
    public static String getTitle(LocalDate date, boolean displayDates, boolean useRelativeTabNames) {

        int diff = Days.daysBetween(LocalDate.now(), date).getDays();
        if (useRelativeTabNames) {
            if (diff == -1) {
                return "Wczoraj";
            } else if (diff == 0) {
                return "Dzisiaj";
            } else if (diff == 1) {
                return "Jutro";
            }
        }

        //TODO "Display dates" preference support
        boolean sameWeek = LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY) == date.withDayOfWeek(DateTimeConstants.MONDAY);
        if (!displayDates && sameWeek) {
            return date.dayOfWeek().getAsText(new Locale("pl"));
        } else {
            return date.toString("d MMM.", new Locale("pl"));
        }

    }
}

