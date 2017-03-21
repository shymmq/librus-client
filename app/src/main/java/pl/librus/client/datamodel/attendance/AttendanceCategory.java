package pl.librus.client.datamodel.attendance;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;
import pl.librus.client.datamodel.Identifiable;

/**
 * Created by Adam on 16.12.2016.
 */

@Entity(builder = ImmutableAttendanceCategory.Builder.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableAttendanceCategory.class)
public abstract class AttendanceCategory implements Persistable, Identifiable {
    @Key
    public abstract String id();

    public abstract String name();

    @Nullable
    public abstract String colorRGB();

    @JsonProperty("Short")
    public abstract String shortName();

    public abstract Boolean standard();

    @JsonProperty("IsPresenceKind")
    public abstract Boolean presenceKind();

    @JsonProperty("Order")
    public abstract int priority();

}
