package pl.librus.client.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Embedded;
import io.requery.Entity;
import io.requery.ManyToOne;
import io.requery.OneToOne;

/**
 * Created by szyme on 30.01.2017.
 * Class representing /Me endpoint
 */
@Value.Immutable
@JsonDeserialize(as=ImmutableMe.class)
public abstract class Me {
    @Value.Parameter
    public abstract LibrusAccount account();

    public static Me create(LibrusAccount account) {
        return ImmutableMe.of(account);
    }

}
