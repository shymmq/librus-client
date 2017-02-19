package pl.librus.client.datamodel;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;

@Entity(builder = ImmutableSubject.Builder.class)
@Value.Immutable
@JsonDeserialize(as=ImmutableSubject.class)
public abstract class Subject implements Persistable, Identifiable {
    @Key
    @Value.Parameter
    public abstract String id();

    @Value.Parameter
    public abstract String name();

}
