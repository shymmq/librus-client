package pl.librus.client.db;

import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import io.requery.meta.Attribute;
import io.requery.proxy.CompositeKey;
import pl.librus.client.datamodel.HasId;
import pl.librus.client.datamodel.Lesson;
import pl.librus.client.datamodel.LessonSubject;
import pl.librus.client.datamodel.LessonTeacher;
import pl.librus.client.datamodel.LessonType;

/**
 * Created by robwys on 05/02/2017.
 */
@RunWith(AndroidJUnit4.class)
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
                .build();

        data.insert(original);
        clearCache();

        //when
        Map<Attribute<Lesson, ?>, Object> map = new LinkedHashMap<>();
        map.put(LessonType.LESSON_NO, lessonNo);
        map.put(LessonType.DATE, date);
        Lesson result = data.findByKey(Lesson.class, new CompositeKey<>(map));

        //then
        Assert.assertThat(result, Matchers.is(original));
    }

}
