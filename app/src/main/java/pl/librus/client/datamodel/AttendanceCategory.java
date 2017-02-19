package pl.librus.client.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;

@Entity(builder = ImmutableAttendanceCategory.Builder.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableAttendanceCategory.class)
public abstract class AttendanceCategory implements Persistable, Identifiable {
    @Key
    public abstract String id();

    public abstract String name();

    @Value.Default
    public String colorRGB() {
        return "FFFFFF";
    }

    @JsonProperty("Short")
    public abstract String shortName();

    public abstract Boolean standard();

    @JsonProperty("IsPresenceKind")
    public abstract Boolean presenceKind();

    @JsonProperty("Order")
    public abstract int priority();

}
