package pl.librus.client.datamodel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;
import pl.librus.client.api.IdDeserializer;

/**
 * Created by Adam on 13.12.2016.
 * Class representing /Attendances item
 */

@Entity
@Value.Immutable
@Value.Style(builder = "new")
@JsonDeserialize(as = ImmutableAttendance.class)
public abstract class Attendance implements Persistable{
    @Key
    public abstract String id();

    @JsonDeserialize(using = IdDeserializer.class)
    public abstract String lesson();

    @JsonDeserialize(using = IdDeserializer.class)
    public abstract String type();

    @JsonDeserialize(using = IdDeserializer.class)
    public abstract String addedBy();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public abstract LocalDate date();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    public abstract LocalDateTime addDate();

    @JsonProperty("LessonNo")
    public abstract int lessonNumber();

    public abstract int semester();

    public static class Builder extends ImmutableAttendance.Builder{
    }

}
