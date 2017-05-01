package pl.librus.client.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;
import org.joda.time.LocalTime;

/**
 * Created by robwys on 01/05/2017.
 */

@Value.Immutable
@JsonDeserialize(builder = ImmutableLessonRange.Builder.class)
public interface LessonRange {

    @Value.Parameter
    LocalTime from();

    @Value.Parameter
    LocalTime to();

    public static LessonRange lessonAt(int hour, int minutes) {
        LocalTime from = LocalTime.MIDNIGHT
                .withHourOfDay(hour)
                .withMinuteOfHour(minutes);
        LocalTime to = from.plusMinutes(45);
        return ImmutableLessonRange.of(from, to);
    }
}
