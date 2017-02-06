package pl.librus.client.datamodel;

import android.support.annotation.NonNull;

import org.immutables.value.Value;
import org.joda.time.LocalDate;

import java.io.Serializable;

import io.requery.Persistable;

/**
 * Created by Adam on 2016-10-31. librus-client
 */

@Value.Immutable

public abstract class Announcement implements Comparable<Announcement>, Identifiable, Serializable, Persistable {
    public abstract String id();
    public abstract LocalDate startDate();
    public abstract LocalDate endDate();
    public abstract String subject();
    public abstract String content();
    public abstract String authorId();

    @Override
    public int compareTo(@NonNull Announcement announcement) {
        return announcement.startDate().compareTo(startDate());
    }


}
