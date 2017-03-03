package pl.librus.client.datamodel.attendance;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Entity;
import io.requery.Persistable;

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
