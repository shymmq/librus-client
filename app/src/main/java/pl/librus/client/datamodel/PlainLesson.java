package pl.librus.client.datamodel;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Embedded;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;
import pl.librus.client.api.IdDeserializer;

@Entity
@Value.Immutable
@Value.Style(builder = "new")
@JsonDeserialize(as = ImmutablePlainLesson.class)
public abstract class PlainLesson implements Persistable, Identifiable{
    @Key
    public abstract String id();

    @JsonDeserialize(using = IdDeserializer.class)
    public abstract String teacher();

    @JsonDeserialize(using = IdDeserializer.class)
    public abstract String subject();

    public static class Builder extends ImmutablePlainLesson.Builder {

    }


}
