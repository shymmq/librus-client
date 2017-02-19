package pl.librus.client.datamodel;

import android.graphics.Color;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;

/**
 * Represents /Colors item
 * Created by szyme on 07.02.2017.
 */

@Entity(builder = ImmutableLibrusColor.Builder.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableLibrusColor.class)
public abstract class LibrusColor implements Persistable, Identifiable {

    @Key
    public abstract String id();

    public abstract String name();

    @JsonProperty("RGB")
    public abstract String rawColor();


    public int colorInt() {
        return Color.parseColor('#' + rawColor());
    }

}
