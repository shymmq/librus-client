package pl.librus.client.datamodel.lesson;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import javax.persistence.Embeddable;

import io.requery.Persistable;
import pl.librus.client.datamodel.subject.Subject;

@Embeddable
@Value.Immutable
@JsonDeserialize(as=ImmutableLessonSubject.class)
public abstract class LessonSubject implements Persistable {
    public abstract String id();

    public abstract String name();

    public static ImmutableLessonSubject.Builder builder() {
        return ImmutableLessonSubject.builder();
    }

    public static LessonSubject fromSubject(Subject subject) {
        return builder()
                .id(subject.id())
                .name(subject.name())
                .build();
    }

}
