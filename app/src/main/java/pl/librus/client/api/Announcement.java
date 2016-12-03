package pl.librus.client.api;

import android.support.annotation.NonNull;

import org.joda.time.LocalDate;

import java.io.Serializable;

/**
 * Created by Adam on 2016-10-31.
 */

public class Announcement implements Serializable, Comparable<Announcement> {
    private final Integer id;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String subject;
    private final String content;
    private final Teacher teacher;
    boolean unread;
    private Integer category = 4; //given by AnnouncementAdapter

    public Announcement(int id, LocalDate startDate, LocalDate endDate, String subject, Teacher teacher, String content, boolean unread) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.subject = subject;
        this.teacher = teacher;
        this.content = content;
        this.unread = unread;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public Integer getId() {
        return id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
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
