package pl.librus.client.datamodel;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.joda.time.LocalTime;

import javax.persistence.Embedded;

import io.requery.Column;
import io.requery.Convert;
import io.requery.Key;
import io.requery.Superclass;
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

}
