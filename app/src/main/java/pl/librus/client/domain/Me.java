package pl.librus.client.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Embedded;
import io.requery.Entity;
import pl.librus.client.data.server.IdDeserializer;

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

    @JsonProperty("Class")
    @JsonDeserialize(using = IdDeserializer.class)
    @Value.Parameter
    public abstract String classId();

    @Override
    public String id() {
        return account().login();
    }
}
