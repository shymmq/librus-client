package pl.librus.client.datamodel.announcement;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Entity;
import io.requery.Persistable;

/**
 * Created by Adam on 2016-10-31. librus-client
 */

@Value.Immutable
@Value.Style(builder = "new")
@Entity
@JsonDeserialize(as = ImmutableAnnouncement.class)
public abstract class Announcement extends BaseAnnouncement implements Comparable<Announcement>, Persistable {

    public static class Builder extends ImmutableAnnouncement.Builder {

    }

    @Override
    public int compareTo(@NonNull Announcement announcement) {
        return announcement.startDate().compareTo(startDate());
    }


}
