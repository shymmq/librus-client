package pl.librus.client.datamodel;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import javax.persistence.Embeddable;

import io.requery.Key;

@Embeddable
@Value.Immutable
@JsonDeserialize(as = ImmutableEmbeddedId.class)
public abstract class EmbeddedId {

    @Value.Parameter
    @Key
    public abstract String id();

    public static EmbeddedId create(String id) {
        return ImmutableEmbeddedId.of(id);
    }

    public static ImmutableEmbeddedId.Builder builder() {
        return ImmutableEmbeddedId.builder();
    }

}
