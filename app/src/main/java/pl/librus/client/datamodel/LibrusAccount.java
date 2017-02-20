package pl.librus.client.datamodel;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import javax.persistence.Embeddable;

import io.requery.Key;

@Embeddable
@Value.Immutable
@Value.Style(builder = "new")
@JsonDeserialize(as = ImmutableLibrusAccount.class)
public abstract class LibrusAccount {

    @Key
    public abstract String login();

    public abstract String firstName();

    public abstract String lastName();

    public abstract String email();

    public String name() {
        return firstName() + " " + lastName();
    }

    public static class Builder extends ImmutableLibrusAccount.Builder {

    }

}
