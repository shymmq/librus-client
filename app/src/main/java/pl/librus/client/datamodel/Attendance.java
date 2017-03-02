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
public abstract class Attendance extends BaseAttendance implements Persistable{

    public static class Builder extends ImmutableAttendance.Builder{
    }

}
