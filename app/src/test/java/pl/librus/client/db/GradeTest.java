package pl.librus.client.db;

import com.google.common.collect.Lists;

import org.hamcrest.Matchers;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import pl.librus.client.datamodel.Grade;
import pl.librus.client.datamodel.HasId;
import pl.librus.client.datamodel.MultipleIds;

/**
 * Created by robwys on 05/02/2017.
 */
@RunWith(RobolectricTestRunner.class)
public class GradeTest extends BaseDBTest {
    @Test
    public void shouldReadGrade() {
        //given
        String id = "abc";
        Grade original = new Grade.Builder()
                .date(LocalDate.now())
                .addDate(LocalDateTime.now())
                .addedBy(HasId.of("12"))
                .category(HasId.of("34"))
                .finalPropositionType(false)
                .finalType(false)
                .grade("4+")
                .id(id)
                .lesson(HasId.of("56"))
                .semester(1)
                .semesterPropositionType(false)
                .semesterType(false)
                .subject(HasId.of("78"))
                .comments(MultipleIds.fromIds(Lists.newArrayList("777", "888")))
                .student(HasId.of("77779"))
                .build();

        data.insert(original);
        clearCache();

        //when
        Grade result = data.findByKey(Grade.class, id);

        //then
        Assert.assertThat(result, Matchers.is(original));
    }

}
