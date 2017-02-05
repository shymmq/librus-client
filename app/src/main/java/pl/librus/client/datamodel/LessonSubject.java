package pl.librus.client.datamodel;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import javax.persistence.Embeddable;

import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;

@Embeddable
@Value.Style(builder = "new")
@Value.Immutable
@JsonDeserialize(as=ImmutableLessonSubject.class)
public abstract class LessonSubject implements Persistable {
    public abstract String id();

    public abstract String name();

    public static class Builder extends ImmutableLessonSubject.Builder {

    }

}
