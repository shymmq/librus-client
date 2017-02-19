package pl.librus.client.datamodel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import io.requery.Embedded;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;

/**
 * Created by Adam on 13.12.2016.
 * Class representing /Attendances item
 */

@Entity(builder = ImmutableAttendance.Builder.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableAttendance.class)
public abstract class Attendance implements Persistable{
    @Key
    public abstract String id();

    @Embedded
    public abstract HasId lesson();

    @Embedded
    public abstract HasId type();

    @Embedded
    public abstract HasId addedBy();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public abstract LocalDate date();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    public abstract LocalDateTime addDate();

    @JsonProperty("LessonNo")
    public abstract int lessonNumber();

    public abstract int semester();

}
