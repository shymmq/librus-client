package pl.librus.client.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Embedded;
import io.requery.Entity;
import io.requery.Persistable;

/**
 * Created by szyme on 30.01.2017.
 * Class representing /Me endpoint
 */
@Entity(builder = ImmutableMe.Builder.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableMe.class)
public abstract class Me implements Identifiable {

    @Embedded
    @Value.Parameter
    public abstract LibrusAccount account();

    @Override
    public String id() {
        return account().login();
    }
}
