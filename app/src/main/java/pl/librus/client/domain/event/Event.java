package pl.librus.client.domain.event;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Entity;

@Entity(builder = ImmutableEvent.Builder.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableEvent.class)
public abstract class Event extends BaseEvent {
}
