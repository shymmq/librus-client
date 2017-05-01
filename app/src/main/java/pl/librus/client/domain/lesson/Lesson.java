package pl.librus.client.domain.lesson;

import org.immutables.value.Value;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import io.requery.Column;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;
import pl.librus.client.domain.Identifiable;

@Entity(builder = ImmutableLesson.Builder.class)
@Value.Immutable
public abstract class Lesson extends BaseLesson implements Identifiable, Comparable<Lesson> {

    @Key
    @Column(name = "\"date\"")
    public abstract LocalDate date();

    @Override
    public String id() {
        return date().toString() + ":" + lessonNo();
    }

    @Override
    public int compareTo(Lesson lesson) {
        return this.lessonNo() - lesson.lessonNo();
    }

    public DateTime toDateTime() {
        return date().toDateTime(hourTo().get());
    }
}
