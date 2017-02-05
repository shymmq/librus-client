package pl.librus.client.datamodel;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import javax.persistence.Embeddable;

import io.requery.Embedded;
import io.requery.Entity;

/**
 * Created by szyme on 31.01.2017.
 */

@Embeddable
@Value.Immutable
@Value.Style(builder = "new")
@JsonDeserialize(as = ImmutableHasId.class)
public abstract class HasId {

    @Value.Parameter
    public abstract String id();

    public static HasId of(String id) {
        return ImmutableHasId.of(id);
    }

    public static class Builder extends ImmutableHasId.Builder{

    }

}
