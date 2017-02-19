package pl.librus.client.datamodel;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Embedded;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;

@Entity(builder = ImmutablePlainLesson.Builder.class)
@Value.Immutable
@JsonDeserialize(as = ImmutablePlainLesson.class)
public abstract class PlainLesson implements Persistable, Identifiable{
    @Key
    public abstract String id();

    @Embedded
    public abstract HasId teacher();

    @Embedded
    public abstract HasId subject();

}
