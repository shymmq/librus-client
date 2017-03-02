package pl.librus.client.datamodel;

import org.immutables.value.Value;

@Value.Immutable
public abstract class FullAnnouncement extends BaseAnnouncement {

    public abstract Teacher addedBy();
}
