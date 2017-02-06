package pl.librus.client.datamodel;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;

/**
 * Created by szyme on 07.12.2016. librus-client
 */
@Entity
@Value.Immutable
@Value.Style(builder = "new")
@JsonDeserialize(as = ImmutableEventCategory.class)
public abstract class EventCategory implements Persistable{
    public abstract String name();

    @Key
    public abstract String id();

    public static class Builder extends ImmutableEventCategory.Builder {

    }

}
