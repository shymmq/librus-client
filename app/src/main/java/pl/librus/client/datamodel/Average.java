package pl.librus.client.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Embedded;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;

/**
 * Created by szyme on 08.12.2016. librus-client
 */
@Entity
@Value.Immutable
@Value.Style(builder = "new")
@JsonDeserialize(as = ImmutableAverage.class)
public abstract class Average implements Persistable {

    public abstract double semester1();

    public abstract double semester2();

    public abstract double fullYear();

    @Embedded
    public abstract EmbeddedId subject();

    public static class Builder extends ImmutableAverage.Builder {

    }

}
