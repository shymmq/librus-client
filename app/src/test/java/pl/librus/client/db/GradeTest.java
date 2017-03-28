package pl.librus.client.db;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import pl.librus.client.domain.grade.Grade;

/**
 * Created by robwys on 05/02/2017.
 */
@RunWith(RobolectricTestRunner.class)
public class GradeTest extends BaseDBTest {
    @Test
    public void shouldReadGrade() {
        //given
        String id = "abc";
        Grade original = EntityTemplates.grade()
                .withId(id);

        data.insert(original);
        clearCache();

        //when
        Grade result = data.findByKey(Grade.class, id);

        //then
        Assert.assertThat(result, equalsNotSameInstance(original));
    }

}
