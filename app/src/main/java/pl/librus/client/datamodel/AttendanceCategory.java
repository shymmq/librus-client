package pl.librus.client.datamodel;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;

/**
 * Created by Adam on 16.12.2016.
 */

@Entity
@Value.Immutable
@Value.Style(builder = "new")
@JsonDeserialize(as = ImmutableAttendanceCategory.class)
public abstract class AttendanceCategory implements Persistable {
    @Key
    public abstract String id();

    public abstract String name();

    @Nullable
    public abstract String colorRGB();

    @JsonProperty("Short")
    public abstract String shortName();

    public abstract boolean standard();

    @JsonProperty("IsPresenceKind")
    public abstract boolean presenceKind();

    @JsonProperty("Order")
    public abstract int priority();

    public static class Builder extends ImmutableAttendanceCategory.Builder {
    }

}
