package pl.librus.client.datamodel;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Embedded;
import io.requery.Entity;
import io.requery.Persistable;

/**
 * Created by szyme on 30.01.2017.
 * Class representing /Me endpoint
 */
@Entity
@Value.Immutable
@Value.Style(builder = "new")
@JsonDeserialize(as = ImmutableMe.class)
public abstract class Me implements Persistable {

    @Embedded
    public abstract LibrusAccount account();

    public static class Builder extends ImmutableMe.Builder {
    }
}
