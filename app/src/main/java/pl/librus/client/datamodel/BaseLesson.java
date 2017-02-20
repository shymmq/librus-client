package pl.librus.client.datamodel;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import javax.persistence.Embedded;

import io.requery.Column;
import io.requery.Convert;
import io.requery.Key;
import io.requery.Superclass;
import pl.librus.client.api.IdDeserializer;
import pl.librus.client.sql.LocalTimeConverter;

/**
 * Created by robwys on 04/02/2017.
 */

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
    public abstract boolean substitutionClass();

    @Column
    @JsonProperty("IsCanceled")
    public abstract boolean cancelled();

    @Convert(LocalTimeConverter.class)
    public abstract LocalTime hourFrom();

    @Convert(LocalTimeConverter.class)
    public abstract LocalTime hourTo();

    @Nullable
    @Column
    public abstract String substitutionNote();

    @Nullable
    @Column
    public abstract LocalDate orgDate();

    @Nullable
    @Column
    public abstract Integer orgLessonNo();

    @Nullable
    @Column
    @JsonDeserialize(using = IdDeserializer.class)
    public abstract String orgLesson();

    @Nullable
    @Column
    @JsonDeserialize(using = IdDeserializer.class)
    public abstract String orgSubject();

    @Nullable
    @Column
    @JsonDeserialize(using = IdDeserializer.class)
    public abstract String orgTeacher();

}
