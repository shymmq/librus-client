package pl.librus.client.datamodel.lesson;

import org.immutables.value.Value;
import org.joda.time.LocalDate;

import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;

@Entity
@Value.Immutable
@Value.Style(builder = "new")
public abstract class Lesson extends BaseLesson implements Persistable, Comparable<Lesson> {

    @Key
    public abstract LocalDate date();

    public static class Builder extends ImmutableLesson.Builder {

    }

    @Override
    public int compareTo(Lesson lesson) {
        return this.lessonNo() - lesson.lessonNo();
    }
}
