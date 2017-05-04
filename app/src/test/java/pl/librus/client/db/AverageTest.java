package pl.librus.client.db;

import org.junit.Assert;
import org.junit.Test;

import pl.librus.client.domain.Average;
import pl.librus.client.domain.AverageType;
import pl.librus.client.domain.ImmutableAverage;

/**
 * Created by robwys on 05/02/2017.
 */
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
