package pl.librus.client.domain.event;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;
import pl.librus.client.domain.Identifiable;

/**
 * Created by szyme on 07.12.2016. librus-client
 */
@Entity(builder = ImmutableEventCategory.Builder.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableEventCategory.class)
public abstract class EventCategory implements Persistable, Identifiable {

    @Key
    public abstract String id();

    public abstract String name();

}
