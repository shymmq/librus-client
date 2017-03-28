package pl.librus.client.domain.grade;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Optional;

import io.requery.Column;
import io.requery.Key;
import io.requery.Superclass;
import pl.librus.client.data.server.IdOptionalDeserializer;
import pl.librus.client.domain.Identifiable;

@Superclass
public abstract class BaseGradeCategory implements Identifiable {

    @Key
    public abstract String id();

    @Column
    public abstract Optional<Integer> weight();

    @Column
    public abstract String name();

    @Column
    @JsonDeserialize(using = IdOptionalDeserializer.class)
    @JsonProperty("Color")
    public abstract Optional<String> colorId();
}
