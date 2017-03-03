package pl.librus.client.datamodel.lesson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;
import org.joda.time.LocalDate;

@Value.Immutable
@JsonDeserialize(as = ImmutableJsonLesson.class)
public abstract class JsonLesson extends BaseLesson {

    public Lesson convert(LocalDate date) {
        return new Lesson.Builder()
                .from(this)
                .date(date)
                .build();
    }
}
