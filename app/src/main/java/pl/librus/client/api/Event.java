package pl.librus.client.api;

import org.joda.time.LocalDate;

import java.io.Serializable;

public class Event implements Serializable {

    private String category;
    private String description;
    private LocalDate date;
    private int lessonNumber;

    Event(String category, String description, LocalDate date, int lessonNumber) {
        this.description = description;
        this.category = category;
        this.date = date;
        this.lessonNumber = lessonNumber;
    }

    public String getCategory() {
        return category;
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
