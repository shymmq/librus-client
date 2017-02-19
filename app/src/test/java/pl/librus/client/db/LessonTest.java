package pl.librus.client.db;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.LinkedHashMap;
import java.util.Map;

import io.requery.meta.Attribute;
import io.requery.proxy.CompositeKey;
import pl.librus.client.datamodel.BaseLesson;
import pl.librus.client.datamodel.HasId;
import pl.librus.client.datamodel.ImmutableJsonLesson;
import pl.librus.client.datamodel.ImmutableLesson;
import pl.librus.client.datamodel.ImmutableLessonSubject;
import pl.librus.client.datamodel.ImmutableLessonTeacher;
import pl.librus.client.datamodel.JsonLesson;
import pl.librus.client.datamodel.Lesson;
import pl.librus.client.datamodel.LessonType;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class LessonTest extends BaseDBTest {
    @Test
    public void shouldReadLesson() {
        //given
        LocalDate date = LocalDate.now();
        int lessonNo = 1;
        Lesson original = ImmutableLesson.builder()
                .from(baseLesson())
                .lessonNo(lessonNo)
                .orgTeacherId("321")
                .date(date)
                .build();

        data.insert(original);
        clearCache();

        //when
        Map<Attribute<Lesson, ?>, Object> map = new LinkedHashMap<>();
        map.put(LessonType.LESSON_NO, lessonNo);
        map.put(LessonType.DATE, date);
        Lesson result = data.findByKey(Lesson.class, new CompositeKey<>(map));

        //then
        assertThat(result, equalsNotSameInstance(original));
    }

    @Test
    public void shouldTransformFromJson() {
        //given
        String teacherId = "321";
        String lessonId = "654";
        String subjectId = "987";
        JsonLesson original = ImmutableJsonLesson.builder()
                .from(baseLesson())
                .orgTeacher(HasId.of(teacherId))
                .orgLesson(HasId.of(lessonId))
                .orgSubject(HasId.of(subjectId))
                .build();

        //when
        Lesson converted = original.convert(LocalDate.now());

        //then
        assertThat(converted.orgTeacherId(), is(teacherId));
        assertThat(converted.orgLessonId(), is(lessonId));
        assertThat(converted.orgSubjectId(), is(subjectId));
    }

    private BaseLesson baseLesson() {
        return ImmutableJsonLesson.builder()
                .lessonNo(3)
                .dayNo(1)
                .teacher(ImmutableLessonTeacher.builder()
                        .firstName("Tomasz")
                        .lastName("Problem")
                        .id("123")
                        .build())
                .subject(ImmutableLessonSubject.builder()
                        .id("456")
                        .name("Matematyka")
                        .build())
                .hourFrom(LocalTime.parse("08:00"))
                .hourTo(LocalTime.parse("08:45"))
                .cancelled(true)
                .substitutionClass(true)
                .substitutionNote("substitution note")
                .build();
    }

}
