package pl.librus.client.db;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import pl.librus.client.domain.ImmutableTeacher;
import pl.librus.client.domain.Teacher;
import pl.librus.client.domain.TeacherType;

@RunWith(RobolectricTestRunner.class)
public class TeacherTest extends BaseDBTest {

    @Test
    public void shouldReadTeacherWithDefaults() {
        //given
        String teacherId = "123";
        Teacher original = ImmutableTeacher.builder()
                .id(teacherId)
                .build();

        data.upsert(original);
        clearCache();

        //when
        Teacher result = data.select(Teacher.class)
                .where(TeacherType.ID.eq(teacherId))
                .get()
                .first();

        //then
        Assert.assertThat(result, equalsNotSameInstance(original));
    }

    @Test
    public void shouldReadTeacherWithNonDefaultValue() {
        //given
        String teacherId = "456";
        Teacher original = ImmutableTeacher.builder()
                .schoolAdministrator(true)
                .id(teacherId)
                .build();

        data.upsert(original);
        clearCache();

        //when
        Teacher result = data.select(Teacher.class)
                .where(TeacherType.ID.eq(teacherId))
                .get()
                .first();

        //then
        Assert.assertThat(result, equalsNotSameInstance(original));
    }

    @Test
    public void shouldReadTeacherWithOptionalPresent() {
        //given
        String teacherId = "456";
        Teacher original = ImmutableTeacher.builder()
                .schoolAdministrator(true)
                .firstName("firstName")
                .id(teacherId)
                .build();

        data.upsert(original);
        clearCache();

        //when
        Teacher result = data.select(Teacher.class)
                .where(TeacherType.ID.eq(teacherId))
                .get()
                .first();

        //then
        Assert.assertThat(result, equalsNotSameInstance(original));
    }

}
