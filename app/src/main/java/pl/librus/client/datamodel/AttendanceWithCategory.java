package pl.librus.client.datamodel;

import android.support.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
public abstract class AttendanceWithCategory extends BaseAttendance {

    public abstract AttendanceCategory category();

}
