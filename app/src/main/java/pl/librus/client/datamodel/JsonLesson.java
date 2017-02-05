package pl.librus.client.datamodel;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;
import org.joda.time.LocalDate;

/**
 * Created by robwys on 04/02/2017.
 */

@Value.Immutable
@JsonDeserialize(as = ImmutableJsonLesson.class)
public abstract class JsonLesson extends BaseLesson {

    @Value.Default
    public boolean substitutionClass(){
        return false;
    };

    @Value.Default
    public boolean cancelled(){
        return false;
    };

    public Lesson convert(LocalDate date) {
        return new Lesson.Builder()
                .from(this)
                .date(date)
                .build();
    }
}
