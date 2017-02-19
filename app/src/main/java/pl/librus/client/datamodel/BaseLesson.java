package pl.librus.client.datamodel;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import javax.persistence.Embedded;

import io.requery.Column;
import io.requery.Key;
import io.requery.Superclass;

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

    @JsonProperty("IsSubstitutionClass")
    @Column
    public abstract Boolean substitutionClass();

    @JsonProperty("IsCanceled")
    @Column
    public abstract Boolean cancelled();

    @Column
    public abstract LocalTime hourFrom();

    @Column
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

}
