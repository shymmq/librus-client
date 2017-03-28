package pl.librus.client.domain.announcement;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Entity;
import io.requery.Persistable;

/**
 * Created by Adam on 2016-10-31. librus-client
 */

@Value.Immutable
@Entity(builder = ImmutableAnnouncement.Builder.class)
@JsonDeserialize(as = ImmutableAnnouncement.class)
public abstract class Announcement extends BaseAnnouncement implements Comparable<Announcement>, Persistable {

    @Override
    public int compareTo(@NonNull Announcement announcement) {
        return announcement.startDate().compareTo(startDate());
    }


}
