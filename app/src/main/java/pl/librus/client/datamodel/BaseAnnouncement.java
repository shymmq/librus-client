package pl.librus.client.datamodel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.joda.time.LocalDate;

import java.io.Serializable;

import io.requery.Column;
import io.requery.Key;
import io.requery.Superclass;
import pl.librus.client.api.IdDeserializer;

@Superclass
public abstract class BaseAnnouncement implements Identifiable, Serializable {

    @Key
    @Column
    public abstract String id();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column
    public abstract LocalDate startDate();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column
    public abstract LocalDate endDate();

    @Column
    public abstract String subject();

    @Column
    public abstract String content();

    @JsonDeserialize(using = IdDeserializer.class)
    @JsonProperty("AddedBy")
    @Column
    public abstract String addedById();
}
