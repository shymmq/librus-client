package pl.librus.client.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Optional;

import org.immutables.value.Value;
import org.joda.time.LocalTime;

/**
 * Created by robwys on 01/05/2017.
 */

@Value.Immutable
@JsonDeserialize(builder = ImmutableLessonRange.Builder.class)
public interface LessonRange {

    @Value.Parameter
    Optional<LocalTime> from();

    @Value.Parameter
    Optional<LocalTime> to();

    public static LessonRange lessonAt(int hour, int minutes) {
        LocalTime from = LocalTime.MIDNIGHT
                .withHourOfDay(hour)
                .withMinuteOfHour(minutes);
        LocalTime to = from.plusMinutes(45);
        return ImmutableLessonRange.builder()
                .from(from)
                .to(to)
                .build();
    }

    public static LessonRange empty() {
        return ImmutableLessonRange.builder()
                .build();
    }

}
