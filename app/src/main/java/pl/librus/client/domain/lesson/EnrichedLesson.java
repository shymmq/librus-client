package pl.librus.client.domain.lesson;

import com.google.common.base.Optional;

import org.immutables.value.Value;
import org.joda.time.LocalDate;

import pl.librus.client.domain.event.FullEvent;

/**
 * Created by robwys on 01/05/2017.
 */

@Value.Immutable
public abstract class EnrichedLesson extends BaseLesson {

    public abstract LocalDate date();

    public abstract Optional<FullEvent> event();

    @Value.Default
    public boolean current() {
        return false;
    }
}

