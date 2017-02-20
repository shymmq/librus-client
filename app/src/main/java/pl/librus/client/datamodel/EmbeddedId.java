package pl.librus.client.datamodel;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import javax.persistence.Embeddable;

import io.requery.Key;

/**
 * Created by robwys on 07/02/2017.
 */

@Embeddable
@Value.Immutable
@Value.Style(builder = "new")
@JsonDeserialize(as = ImmutableEmbeddedId.class)
public abstract class EmbeddedId {

    @Value.Parameter
    @Key
    public abstract String id();

    public static EmbeddedId of(String id) {
        return ImmutableEmbeddedId.of(id);
    }

    public static class Builder extends ImmutableEmbeddedId.Builder{

    }
}
