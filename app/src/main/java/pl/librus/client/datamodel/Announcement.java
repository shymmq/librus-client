package pl.librus.client.datamodel;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;
import org.joda.time.LocalDate;

import java.io.Serializable;

import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;
import pl.librus.client.api.IdDeserializer;

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
