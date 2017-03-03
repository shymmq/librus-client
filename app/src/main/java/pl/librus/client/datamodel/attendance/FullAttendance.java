package pl.librus.client.datamodel.attendance;

import android.support.annotation.Nullable;

import org.immutables.value.Value;

import pl.librus.client.datamodel.subject.Subject;
import pl.librus.client.datamodel.Teacher;

@Value.Immutable
public abstract class FullAttendance extends BaseAttendance {

    public abstract AttendanceCategory category();

    @Nullable
    public abstract Teacher addedBy();

    @Nullable
    public abstract Subject subject();
}
