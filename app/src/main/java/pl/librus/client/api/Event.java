package pl.librus.client.api;

import org.joda.time.LocalDate;

import java.io.Serializable;

public class Event implements Serializable {

    static final long serialVersionUID = -5781381587858850733L;
    private String categoryId;
    private String description;
    private LocalDate date;
    private int lessonNumber;

    Event(String categoryId, String description, LocalDate date, int lessonNumber) {
        this.description = description;
        this.categoryId = categoryId;
        this.date = date;
        this.lessonNumber = lessonNumber;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getLessonNumber() {
        return lessonNumber;
    }
}
