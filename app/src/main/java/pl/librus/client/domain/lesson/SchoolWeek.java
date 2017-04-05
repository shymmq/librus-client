package pl.librus.client.domain.lesson;

import org.immutables.value.Value;
import org.joda.time.LocalDate;

import java.util.List;

@Value.Immutable
public interface SchoolWeek {
    @Value.Parameter
    LocalDate weekStart();

    @Value.Parameter
    List<Lesson> lessons();
}
