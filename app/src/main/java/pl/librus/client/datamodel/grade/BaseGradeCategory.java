package pl.librus.client.datamodel.grade;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.requery.Column;
import io.requery.Key;
import io.requery.Superclass;
import pl.librus.client.api.IdDeserializer;
import pl.librus.client.datamodel.Identifiable;

@Superclass
public abstract class BaseGradeCategory implements Identifiable {

    @Key
    public abstract String id();

    @Nullable
    @Column
    public abstract Integer weight();

    @Column
    public abstract String name();

    @Column
    @JsonDeserialize(using = IdDeserializer.class)
    @JsonProperty("Color")
    public abstract String colorId();
}
