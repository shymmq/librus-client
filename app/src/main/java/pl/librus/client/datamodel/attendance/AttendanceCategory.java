package pl.librus.client.datamodel.attendance;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Optional;

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

    public abstract Optional<String> colorRGB();

    @JsonProperty("Short")
    public abstract String shortName();

    @JsonProperty("IsPresenceKind")
    public abstract Boolean presenceKind();

}
