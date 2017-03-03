package pl.librus.client.db;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.LinkedHashMap;
import java.util.Map;

import io.requery.meta.Attribute;
import io.requery.proxy.CompositeKey;
import pl.librus.client.datamodel.lesson.Lesson;
import pl.librus.client.datamodel.lesson.LessonSubject;
import pl.librus.client.datamodel.lesson.LessonTeacher;
import pl.librus.client.datamodel.lesson.LessonType;

@RunWith(RobolectricTestRunner.class)
public class LessonTest extends BaseDBTest {
    @Test
    public void shouldReadLesson() {
        //given
        LocalDate date = LocalDate.now();
        int lessonNo = 1;
        final Lesson original = new Lesson.Builder()
                .date(LocalDate.now())
                .lessonNo(lessonNo)
                .dayNo(1)
                .teacher(new LessonTeacher.Builder()
                    .firstName("Tomasz")
                    .lastName("Problem")
                    .id("123")
                    .build())
                .subject(new LessonSubject.Builder()
                    .id("456")
                    .name("Matematyka")
                    .build())
                .hourFrom(LocalTime.parse("08:00"))
                .hourTo(LocalTime.parse("08:45"))
                .cancelled(true)
                .substitutionClass(true)
                .orgLessonNo(3)
                .orgLesson("5443")
                .orgDate(LocalDate.parse("2017-02-02"))
                .orgSubject("4432")
                .orgTeacher("7584")
                .substitutionNote("zastępstwo")
                .build();

        data.insert(original);
        clearCache();

        //when
        Map<Attribute<Lesson, ?>, Object> map = new LinkedHashMap<>();
        map.put(LessonType.LESSON_NO, lessonNo);
        map.put(LessonType.DATE, date);
        Lesson result = data.findByKey(Lesson.class, new CompositeKey<>(map));

        //then
        Assert.assertThat(result, equalsNotSameInstance(original));
    }

}
