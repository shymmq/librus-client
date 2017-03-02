package pl.librus.client.datamodel;

import android.support.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
public abstract class FullAttendance extends BaseAttendance {

    public abstract AttendanceCategory category();

    @Nullable
    public abstract Teacher addedBy();

    @Nullable
    public abstract Subject subject();
}
