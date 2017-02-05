package pl.librus.client.datamodel;

import android.database.Observable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.android.gms.common.data.DataBufferObserver;

import org.immutables.value.Value;

import io.requery.Entity;
import io.requery.ForeignKey;
import io.requery.Key;
import io.requery.Persistable;

@Entity
@Value.Immutable
@Value.Style(builder = "new")
@JsonDeserialize(as=ImmutableLibrusAccount.class)
public abstract class LibrusAccount implements Persistable{

    @Key
    public abstract String login();

    public abstract String firstName();

    public abstract String lastName();

    public abstract String email();

    public static class Builder extends ImmutableLibrusAccount.Builder {

    }

    public String name() {
        return firstName() + " " + lastName();
    }

}
