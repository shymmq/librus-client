package pl.librus.client.timetable;

import com.google.common.collect.Lists;

import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.List;
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

    private static int getTabIndex(LocalDate date) {
        LocalDate start = getWeekStart();
        if (!date.isBefore(start) && !date.isAfter(start.plusWeeks(1))) {
            return date.getDayOfWeek() - 1;
        } else {
            return date.getDayOfWeek() + 4;
        }
    }

    //returns first week for which to start downloading

    private static LocalDate getWeekStart() {
        return LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY);
    }

    public static int getTabCount() {
//        return Days.daysBetween(getStartDate(), getWeekStart().plusWeeks(2)).getDays() - 4;
        return getNextFullWeekStarts(LocalDate.now()).size() * 7;
    }

    //returns index for given date
    public static LocalDate getTabDate(int index) {
        return getNextFullWeekStarts(LocalDate.now()).get(0).plusDays(index);
    }

    //return title String for given date
    static String getTitle(LocalDate date, boolean displayDates, boolean useRelativeTabNames) {

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

    //we want to get next 5 days starting from today. this period can overlap one or two weeks. this method returns start days of those weeks
    public static List<LocalDate> getNextFullWeekStarts(LocalDate today) {
//        int weekDay = today.getDayOfWeek();
//        if (weekDay == DateTimeConstants.MONDAY) {
//            return Lists.newArrayList(today);
//        } else if (weekDay == DateTimeConstants.SATURDAY) {
//            return Lists.newArrayList(today.plusDays(2));
//        } else if (weekDay == DateTimeConstants.SUNDAY) {
//            return Lists.newArrayList(today.plusDays(1));
//        } else {
//            LocalDate prevMonday = today.withDayOfWeek(DateTimeConstants.MONDAY);
//            return Lists.newArrayList(prevMonday, prevMonday.plusWeeks(1));

        LocalDate lastMonday = today.withDayOfWeek(DateTimeConstants.MONDAY);
        return Lists.newArrayList(lastMonday, lastMonday.plusWeeks(1));
    }

    static LocalDate getLastFullWeekStart(LocalDate today) {
        LocalDate lastMonday = today.withDayOfWeek(DateTimeConstants.MONDAY);
        return lastMonday.plusWeeks(1);
    }

    static LocalDate getFirstFullWeekStart(LocalDate today) {
        return today.withDayOfWeek(DateTimeConstants.MONDAY);
    }
//    public LocalDate getLastWeekStartDate() {
//        LocalDate res = new LocalDate(0);
//        for (SchoolWeek week : schoolWeeks) {
//            LocalDate weekStart = week.getWeekStart();
//            res = weekStart.isAfter(res) ? weekStart : res;
//        }
//        return res;
//    }
}

