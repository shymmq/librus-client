package pl.librus.client.db;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import pl.librus.client.datamodel.ImmutableLuckyNumber;
import pl.librus.client.datamodel.LuckyNumber;
import pl.librus.client.datamodel.LuckyNumberType;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by robwys on 05/02/2017.
 */

@RunWith(RobolectricTestRunner.class)
public class LuckyNumberTest extends BaseDBTest{


    @Test
    public void shouldReadLuckyNumber() {
        //given
        LocalDate date = LocalDate.now();
        LuckyNumber original = ImmutableLuckyNumber.of(date, 12);
        data.insert(original);
        clearCache();

        //when
        LuckyNumber res = data.findByKey(LuckyNumber.class, date);

        //then
        assertThat(res, equalsNotSameInstance(original));
    }

    @Test
    public void shouldFindCorrectLuckyNumber() {
        //given
        LocalDate date = LocalDate.now();
        LuckyNumber current = ImmutableLuckyNumber.of(date, 12);
        LuckyNumber previous = ImmutableLuckyNumber.of(date.minusDays(1), 25);
        data.insert(current);
        data.insert(previous);
        clearCache();

        //when
        LuckyNumber res = data.findByKey(LuckyNumber.class, date);

        //then
        assertThat(res, equalsNotSameInstance(current));
    }

    @Test
    public void shouldLatestLuckyNumber() {
        //given
        LocalDate date = LocalDate.now();
        LuckyNumber latest = ImmutableLuckyNumber.of(date.minusDays(2), 12);
        LuckyNumber previous = ImmutableLuckyNumber.of(date.minusDays(3), 25);
        data.insert(latest);
        data.insert(previous);
        clearCache();

        //when
        LuckyNumber res = data.select(LuckyNumber.class)
                .where(LuckyNumberType.DAY.lte(date))
                .orderBy(LuckyNumberType.DAY.desc())
                .limit(1)
                .get()
                .first();

        //then
        assertThat(res, equalsNotSameInstance(latest));
    }
}
