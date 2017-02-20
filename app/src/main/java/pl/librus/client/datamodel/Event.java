package pl.librus.client.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;
import org.joda.time.LocalDate;

import io.requery.Embedded;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;

@Entity
@Value.Immutable
@Value.Style(builder = "new")
@JsonDeserialize(as=ImmutableEvent.class)
public abstract class Event implements Persistable, Identifiable{
    @Key
    public abstract String id();

    public abstract String content();

    public abstract LocalDate date();

    @Embedded
    public abstract HasId category();

    public abstract int lessonNo();

    @JsonProperty("CreatedBy")
    @Embedded
    public abstract HasId addedBy();

    public static class Builder extends ImmutableEvent.Builder{
    }

}
