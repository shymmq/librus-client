package pl.librus.client.datamodel.attendance;

import com.google.common.base.Optional;

import org.immutables.value.Value;

import pl.librus.client.datamodel.Teacher;
import pl.librus.client.datamodel.subject.Subject;

@Value.Immutable
public abstract class FullAttendance extends BaseAttendance {

    public abstract AttendanceCategory category();

    public abstract Optional<Teacher> addedBy();

    public abstract Optional<Subject> subject();

    public Optional<String> addedByName() {
        return addedBy().isPresent() ?
                addedBy().get().name() : Optional.absent();
    }
}
