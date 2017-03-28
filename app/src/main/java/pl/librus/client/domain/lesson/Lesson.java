package pl.librus.client.domain.lesson;

import org.immutables.value.Value;
import org.joda.time.LocalDate;

import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;

@Entity(builder = ImmutableLesson.Builder.class)
@Value.Immutable
public abstract class Lesson extends BaseLesson implements Persistable, Comparable<Lesson> {

    @Key
    public abstract LocalDate date();

    @Override
    public int compareTo(Lesson lesson) {
        return this.lessonNo() - lesson.lessonNo();
    }
}
