package pl.librus.client.db;

import org.junit.Assert;
import org.junit.Test;

import pl.librus.client.domain.LibrusUnit;

/**
 * Created by robwys on 01/05/2017.
 */

public class LibrusUnitTest extends BaseDBTest {

    @Test
    public void shouldWriteAndRead() {
        //given
        LibrusUnit unit = EntityTemplates.unit();

        //when
        data.upsert(unit);
        clearCache();
        LibrusUnit result = data.findByKey(LibrusUnit.class, unit.id());

        //then
        Assert.assertThat(result, equalsNotSameInstance(unit));

    }
}
