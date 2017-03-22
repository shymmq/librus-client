package pl.librus.client.datamodel.lesson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Optional;

import org.immutables.value.Value;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import javax.persistence.Embedded;

import io.requery.Column;
import io.requery.Convert;
import io.requery.Key;
import io.requery.Superclass;
import pl.librus.client.api.IdOptionalDeserializer;
import pl.librus.client.sql.LocalTimeConverter;

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
    public Boolean substitutionClass(){
        return false;
    };

    @Column
    @JsonProperty("IsCanceled")
    @Value.Default
    public Boolean cancelled(){
        return false;
    };

    @Convert(LocalTimeConverter.class)
    public abstract LocalTime hourFrom();

    @Convert(LocalTimeConverter.class)
    public abstract LocalTime hourTo();

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
