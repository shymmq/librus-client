package pl.librus.client.datamodel;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Embedded;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;
import pl.librus.client.api.IdDeserializer;

@Entity
@Value.Immutable
@Value.Style(builder = "new")
@JsonDeserialize(as = ImmutableGradeCategory.class)
public abstract class GradeCategory implements Persistable, Identifiable {

    @Key
    public abstract String id();

    @Nullable
    public abstract Integer weight();

    public abstract String name();

    @JsonDeserialize(using = IdDeserializer.class)
    public abstract String color();

    public static class Builder extends ImmutableGradeCategory.Builder {

    }

}
