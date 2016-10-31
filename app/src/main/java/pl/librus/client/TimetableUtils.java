package pl.librus.client;

import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.Locale;

class TimetableUtils {

    static int getStartTab() {
        int weekday = LocalDate.now().getDayOfWeek();
        if (weekday >= DateTimeConstants.SATURDAY) {
            return weekday - 1 + 5;
        } else {
            return weekday - 1;
        }
    }

    static LocalDate getWeekStart() {
        return LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY);
    }

    static int getDayCount() {
//        return Days.daysBetween(getStartDate(), getWeekStart().plusWeeks(2)).getDays() - 4;
        return 10;
    }

    static String getTabTitle(int index, boolean displayDates, boolean useRelativeTabNames) {
        return getTitle(getTabDate(index), displayDates, useRelativeTabNames);
    }

    static LocalDate getTabDate(int index) {
        LocalDate weekStart = getWeekStart();
        if (index >= 5) {
            return weekStart.plusDays(index + 2);
        }
        return weekStart.plusDays(index);
    }

    private static String getTitle(LocalDate date, boolean displayDates, boolean useRelativeTabNames) {

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

        if (!displayDates) {
            return date.dayOfWeek().getAsText(new Locale("pl"));
        } else {
            return date.toString("d MMM.", new Locale("pl"));
        }

    }
}

