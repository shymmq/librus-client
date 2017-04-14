package pl.librus.client.domain.lesson;

import com.google.common.base.Optional;

import org.immutables.value.Value;
import org.joda.time.LocalDate;

import pl.librus.client.domain.Teacher;

/**
 * Created by robwys on 14/04/2017.
 */

@Value.Immutable
public abstract class FullLesson extends BaseLesson {

    public abstract LocalDate date();

    public abstract Optional<Teacher> orgTeacher();
}
