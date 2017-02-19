package pl.librus.client.datamodel;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.immutables.value.Value;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import io.requery.Column;
import io.requery.Convert;
import io.requery.Embedded;
import io.requery.Entity;
import io.requery.ForeignKey;
import io.requery.Index;
import io.requery.Key;
import io.requery.ManyToOne;
import io.requery.OneToOne;
import io.requery.Persistable;
import pl.librus.client.sql.LocalDateConverter;
import pl.librus.client.sql.LocalTimeConverter;

@Entity(builder = ImmutableLesson.Builder.class)
@Value.Immutable
public abstract class Lesson extends BaseLesson implements Persistable, Comparable<Lesson> {

    @Key
    public abstract LocalDate date();

    @Nullable
    public abstract String orgLessonId();

    @Nullable
    public abstract String orgSubjectId();

    @Nullable
    public abstract String orgTeacherId();

    @Override
    public int compareTo(Lesson lesson) {
        return this.lessonNo() - lesson.lessonNo();
    }
}
