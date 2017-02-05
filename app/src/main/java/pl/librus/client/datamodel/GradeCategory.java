package pl.librus.client.datamodel;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Optional;

import org.immutables.value.Value;

import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;

@Entity
@Value.Immutable
@Value.Style(builder = "new")
@JsonDeserialize(as = ImmutableGradeCategory.class)
public abstract class GradeCategory implements Persistable{

    @Nullable
    public abstract Integer weight();

    public abstract String name();

    @Key
    public abstract String id();

    public static class Builder extends ImmutableGradeCategory.Builder {

    }
}
