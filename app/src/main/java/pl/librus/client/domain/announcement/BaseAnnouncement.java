package pl.librus.client.domain.announcement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Optional;

import org.joda.time.LocalDate;

import java.io.Serializable;

import io.requery.Column;
import io.requery.Key;
import io.requery.Superclass;
import pl.librus.client.data.server.IdOptionalDeserializer;
import pl.librus.client.domain.Identifiable;

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

    @JsonDeserialize(using = IdOptionalDeserializer.class)
    @JsonProperty("AddedBy")
    @Column
    public abstract Optional<String> addedById();
}
