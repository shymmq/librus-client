package pl.librus.client.domain;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;
import pl.librus.client.data.server.IdDeserializer;

@Entity(builder = ImmutablePlainLesson.Builder.class)
@Value.Immutable
@JsonDeserialize(as = ImmutablePlainLesson.class)
public abstract class PlainLesson implements Persistable, Identifiable {
    @Key
    public abstract String id();

    @JsonDeserialize(using = IdDeserializer.class)
    public abstract String teacher();

    @JsonDeserialize(using = IdDeserializer.class)
    public abstract String subject();

}
