package pl.librus.client;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import pl.librus.client.timetable.TimetableUtils;

/**
 * Created by szyme on 18.12.2016. librus-client
 */

public class TimetableUtilTest {
    @Test
    public void shouldReturnPrevAndNextMonday() {
        //given
        LocalDate today = new LocalDate(2016, 12, 20);
        LocalDate prevMonday = new LocalDate(2016, 12, 19);
        LocalDate nextMonday = new LocalDate(2016, 12, 26);
        //when
        List<LocalDate> result = TimetableUtils.getNextFullWeekStarts(today);
        //then
        Assert.assertEquals(Lists.newArrayList(prevMonday, nextMonday), result);
    }
}
