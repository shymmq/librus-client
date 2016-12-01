package pl.librus.client.timetable;

import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.Locale;

public class TimetableUtils {

    //returns default show tab index
    public static int getDefaultTab() {
        int weekday = LocalDate.now().getDayOfWeek();
        if (weekday >= DateTimeConstants.SATURDAY) {
            return getTabIndex(LocalDate.now().plusWeeks(1).withDayOfWeek(DateTimeConstants.MONDAY));
        } else {
            return getTabIndex(LocalDate.now());
        }

    }

    static int getTabIndex(LocalDate date) {
        LocalDate start = getWeekStart();
        if (!date.isBefore(start) && !date.isAfter(start.plusWeeks(1))) {
            return date.getDayOfWeek() - 1;
        } else {
            return date.getDayOfWeek() + 4;
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
        boolean sameWeek = (LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY).isEqual(date.withDayOfWeek(DateTimeConstants.MONDAY)));

        if (useRelativeTabNames) {
            switch (diff) {
                case -1:
                    return "Wczoraj";
                case 0:
                    return "Dzisiaj";
                case 1:
                    return "Jutro";
                default:
                    return !displayDates && sameWeek ? date.dayOfWeek().getAsText(new Locale("pl")) : date.toString("d MMM.", new Locale("pl"));
            }
        } else {
            return !displayDates && sameWeek ? date.dayOfWeek().getAsText(new Locale("pl")) : date.toString("d MMM.", new Locale("pl"));
        }
    }
}

