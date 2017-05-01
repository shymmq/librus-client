package pl.librus.client.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Entity;
import io.requery.Key;
import pl.librus.client.data.server.IdDeserializer;

/**
 * Created by robwys on 01/05/2017.
 */

@Value.Immutable
@Entity(builder = ImmutableLibrusClass.Builder.class)
@JsonDeserialize(builder = ImmutableLibrusClass.Builder.class)
public abstract class LibrusClass implements Identifiable {

    @Key
    public abstract String id();

    public abstract int number();

    public abstract String symbol();

    @JsonDeserialize(using = IdDeserializer.class)
    public abstract String unit();

}
