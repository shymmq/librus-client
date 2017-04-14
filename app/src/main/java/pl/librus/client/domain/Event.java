package pl.librus.client.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;
import org.joda.time.LocalDate;

import io.requery.Column;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;
import pl.librus.client.data.server.IdDeserializer;

@Entity(builder = ImmutableEvent.Builder.class)
@Value.Immutable
@JsonDeserialize(as=ImmutableEvent.class)
public abstract class Event implements Persistable, Identifiable{
    @Key
    public abstract String id();

    public abstract String content();

    @Column(name = "\"date\"")
    public abstract LocalDate date();

    @JsonDeserialize(using = IdDeserializer.class)
    public abstract String category();

    public abstract int lessonNo();

    @JsonProperty("CreatedBy")
    @JsonDeserialize(using = IdDeserializer.class)
    public abstract String addedBy();

}
