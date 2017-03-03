package pl.librus.client.datamodel.attendance;

import org.immutables.value.Value;

@Value.Immutable
public abstract class EnrichedAttendance extends BaseAttendance {

    public abstract AttendanceCategory category();

}
