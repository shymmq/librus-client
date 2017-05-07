package pl.librus.client.domain.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.joda.time.LocalDate;

import io.requery.Column;
import io.requery.Key;
import io.requery.Superclass;
import pl.librus.client.data.server.IdDeserializer;
import pl.librus.client.domain.Identifiable;

/**
 * Created by szyme on 07.05.2017.
 */
@Superclass
public abstract class BaseEvent implements Identifiable {
    @Key
    public abstract String id();

    public abstract String content();

    @Column(name = "\"date\"")
    public abstract LocalDate date();

    @JsonProperty("Category")
    @JsonDeserialize(using = IdDeserializer.class)
    public abstract String categoryId();

    public abstract int lessonNo();

    @JsonProperty("CreatedBy")
    @JsonDeserialize(using = IdDeserializer.class)
    public abstract String addedById();
}
