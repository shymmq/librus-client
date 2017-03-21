package pl.librus.client.db;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import pl.librus.client.datamodel.Average;
import pl.librus.client.datamodel.AverageType;
import pl.librus.client.datamodel.ImmutableAverage;
import pl.librus.client.ui.MainApplication;

/**
 * Created by robwys on 05/02/2017.
 */
@RunWith(RobolectricTestRunner.class)
public class AverageTest extends BaseDBTest {
    @Test
    public void shouldReadAverage() {
        //given
        String subjectId = "123";
        final Average original = ImmutableAverage.builder()
                .fullYear(4.18)
                .semester1(3.66)
                .semester2(0)
                .subject(subjectId)
                .build();

        data.upsert(original);
        clearCache();

        //when
        Average result = data.select(Average.class)
                .where(AverageType.SUBJECT.eq(subjectId))
                .get()
                .first();

        //then
        Assert.assertThat(result, equalsNotSameInstance(original));
    }

}
