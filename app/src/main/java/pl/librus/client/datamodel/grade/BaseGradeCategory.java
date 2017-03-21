package pl.librus.client.datamodel.grade;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Optional;

import io.requery.Column;
import io.requery.Key;
import io.requery.Superclass;
import pl.librus.client.api.IdDeserializer;
import pl.librus.client.datamodel.Identifiable;

@Superclass
public abstract class BaseGradeCategory implements Identifiable {

    @Key
    public abstract String id();

    @Column
    public abstract Optional<Integer> weight();

    @Column
    public abstract String name();

    @Column
    @JsonDeserialize(using = IdDeserializer.class)
    @JsonProperty("Color")
    public abstract Optional<String> colorId();
}
