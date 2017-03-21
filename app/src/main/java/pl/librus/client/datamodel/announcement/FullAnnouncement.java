package pl.librus.client.datamodel.announcement;

import com.google.common.base.Optional;

import org.immutables.value.Value;

import pl.librus.client.datamodel.Teacher;

@Value.Immutable
public abstract class FullAnnouncement extends BaseAnnouncement {

    public abstract Optional<Teacher> addedBy();

    public Optional<String> addedByName() {
        return addedBy().isPresent() ?
                addedBy().get().name() : Optional.absent();
    }
}
