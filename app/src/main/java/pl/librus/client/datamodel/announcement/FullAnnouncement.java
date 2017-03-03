package pl.librus.client.datamodel.announcement;

import org.immutables.value.Value;

import pl.librus.client.datamodel.Teacher;

@Value.Immutable
public abstract class FullAnnouncement extends BaseAnnouncement {

    public abstract Teacher addedBy();
}
