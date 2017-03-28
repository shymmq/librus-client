package pl.librus.client.domain.announcement;

import com.google.common.base.Optional;

import org.immutables.value.Value;

import pl.librus.client.domain.Teacher;

@Value.Immutable
public abstract class FullAnnouncement extends BaseAnnouncement {

    public abstract Optional<Teacher> addedBy();

    public Optional<String> addedByName() {
        return addedBy().isPresent() ?
                addedBy().get().name() : Optional.absent();
    }
}
