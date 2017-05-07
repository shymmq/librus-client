package pl.librus.client.domain;

import org.immutables.value.Value;
import org.joda.time.LocalDate;

/**
 * Created by szyme on 24.04.2017.
 */

@Value.Immutable
abstract class LessonKey {

    @Value.Parameter
    abstract LocalDate date();

    @Value.Parameter
    abstract int lessonNumber();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LessonKey lessonKey = (LessonKey) o;

        return lessonNumber() == lessonKey.lessonNumber() && date().equals(lessonKey.date());

    }

    @Override
    public int hashCode() {
        int result = date().hashCode();
        result = 31 * result + lessonNumber();
        return result;
    }
}