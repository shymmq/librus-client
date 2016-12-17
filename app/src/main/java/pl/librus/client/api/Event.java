package pl.librus.client.api;

import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.Objects;

public class Event implements Serializable {

    private static final long serialVersionUID = -5781381587858850733L;
    private String id, categoryId, addedById, description;
    private LocalDate date;
    private int lessonNumber;

    Event(String id, String categoryId, String description, LocalDate date, String addedById, int lessonNumber) {
        this.id = id;
        this.description = description;
        this.categoryId = categoryId;
        this.date = date;
        this.addedById = addedById;
        this.lessonNumber = lessonNumber;
    }

    public String getAddedById() {
        return addedById;
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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Event && Objects.equals(((Event) obj).getId(), id);
    }

    public int getChanges(Event event) {
        return 0;
    }

    public String getId() {
        return id;
    }
}
