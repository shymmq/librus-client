package pl.librus.client.domain.lesson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Optional;

import org.immutables.value.Value;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import javax.persistence.Embedded;

import io.requery.Column;
import io.requery.Key;
import io.requery.Superclass;
import pl.librus.client.data.server.IdOptionalDeserializer;

@Superclass
public abstract class BaseLesson {

    @Key
    public abstract int lessonNo();

    @Column
    public abstract int dayNo();

    @Embedded
    public abstract LessonSubject subject();

    @Embedded
    public abstract LessonTeacher teacher();

    @Column
    @JsonProperty("IsSubstitutionClass")
    @Value.Default
    public Boolean substitutionClass() {
        return false;
    }

    @Column
    @JsonProperty("IsCanceled")
    @Value.Default
    public Boolean cancelled() {
        return false;
    }

    @Column
    public abstract Optional<LocalTime> hourFrom();

    @Column
    public abstract Optional<LocalTime> hourTo();

    @Column
    public abstract Optional<String> substitutionNote();

    @Column
    public abstract Optional<LocalDate> orgDate();

    @Column
    public abstract Optional<Integer> orgLessonNo();

    @Column
    @JsonDeserialize(using = IdOptionalDeserializer.class)
    public abstract Optional<String> orgLesson();

    @Column
    @JsonDeserialize(using = IdOptionalDeserializer.class)
    public abstract Optional<String> orgSubject();

    @Column
    @JsonDeserialize(using = IdOptionalDeserializer.class)
    public abstract Optional<String> orgTeacher();

}
