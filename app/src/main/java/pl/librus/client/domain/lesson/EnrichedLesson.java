package pl.librus.client.domain.lesson;

import org.immutables.value.Value;
import org.joda.time.LocalDate;

/**
 * Created by robwys on 01/05/2017.
 */

@Value.Immutable
public abstract class EnrichedLesson extends BaseLesson {

    public abstract LocalDate date();

    @Value.Default
    public boolean current() {
        return false;
    }

    public static ImmutableEnrichedLesson fromLesson(Lesson l) {
        return ImmutableEnrichedLesson.builder()
                .from(l)
                .date(l.date())
                .build();
    }
}

