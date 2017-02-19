package pl.librus.client.datamodel;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Optional;

import org.immutables.value.Value;
import org.joda.time.LocalDate;

@Value.Immutable
@JsonDeserialize(as = ImmutableJsonLesson.class)
public abstract class JsonLesson extends BaseLesson {

    @Nullable
    public abstract HasId orgLesson();

    @Nullable
    public abstract HasId orgSubject();

    @Nullable
    public abstract HasId orgTeacher();

    public Lesson convert(LocalDate date) {
        return ImmutableLesson.builder()
                .from(this)
                .orgLessonId(nullableId(orgLesson()))
                .orgSubjectId(nullableId(orgSubject()))
                .orgTeacherId(nullableId(orgTeacher()))
                .date(date)
                .build();
    }

    private String nullableId(HasId hasId){
        return Optional.fromNullable(hasId)
                .transform(HasId::id)
                .orNull();
    }
}
