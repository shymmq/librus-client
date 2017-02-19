package pl.librus.client.datamodel;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import javax.persistence.Embeddable;

/**
 * Created by szyme on 31.01.2017.
 * A class representing an id value wrapped in a JSONObject; for use with Jackson
 */

@Embeddable
@Value.Immutable
@JsonDeserialize(as = ImmutableHasId.class)
public abstract class HasId {

    public static HasId of(String id) {
        return ImmutableHasId.of(id);
    }

    @Value.Parameter
    public abstract String id();

    @Override
    public String toString() {
        return id();
    }

    public static ImmutableHasId.Builder builder() {
        return ImmutableHasId.builder();
    }

}
