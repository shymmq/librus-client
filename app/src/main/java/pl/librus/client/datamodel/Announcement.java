package pl.librus.client.datamodel;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;
import org.joda.time.LocalDate;

import java.io.Serializable;

import io.requery.Embedded;
import io.requery.Entity;
import io.requery.Persistable;

/**
 * Created by Adam on 2016-10-31. librus-client
 */

@Value.Immutable
@Value.Style(builder = "new")
@Entity
@JsonDeserialize(as = ImmutableAnnouncement.class)
public abstract class Announcement implements Comparable<Announcement>, Identifiable, Persistable, Serializable {

    public abstract String id();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public abstract LocalDate startDate();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public abstract LocalDate endDate();

    public abstract String subject();

    public abstract String content();

    @Embedded
    public abstract HasId addedBy();

    public static class Builder extends ImmutableAnnouncement.Builder {

    }

    @Override
    public int compareTo(@NonNull Announcement announcement) {
        return announcement.startDate().compareTo(startDate());
    }


}
