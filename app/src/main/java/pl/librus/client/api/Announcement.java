package pl.librus.client.api;

import android.support.annotation.NonNull;

import org.joda.time.LocalDate;

import java.io.Serializable;

/**
 * Created by Adam on 2016-10-31. librus-client
 */

public class Announcement implements Serializable, Comparable<Announcement> {
    static final long serialVersionUID = -3384390935483292393L;
    private final String id;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String subject;
    private final String content;
    private final String authorId;
    private Integer category = 4; //given by AnnouncementAdapter

    Announcement(String id, LocalDate startDate, LocalDate endDate, String subject, String content, String authorId) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.subject = subject;
        this.authorId = authorId;
        this.content = content;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public String getAuthorId() {
        return authorId;
    }

    @Override
    public int compareTo(@NonNull Announcement announcement) {
        int v1 = announcement.getCategory().compareTo(category);
        if (v1 == 0) {
            return announcement.getStartDate().compareTo(startDate);
        }
        return v1;

    }


}
