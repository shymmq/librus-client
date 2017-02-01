package pl.librus.client.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.LocalDate;

import pl.librus.client.datamodel.HasId;

@DatabaseTable(tableName = "events")
public class Event {
    @DatabaseField(id = true)
    @JsonProperty("Id")
    private String id;
    @DatabaseField
    private String content;
    @DatabaseField
    private LocalDate date;
    @DatabaseField
    private HasId category;
    @DatabaseField
    private int lessonNo;
    @DatabaseField
    @JsonProperty("CreatedBy")
    private HasId addedBy;

    public Event() {
    }

    public String getContent() {
        return content;
    }

    public LocalDate getDate() {
        return date;
    }

    public HasId getCategory() {
        return category;
    }

    public int getLessonNo() {
        return lessonNo;
    }

    public HasId getAddedBy() {
        return addedBy;
    }

    public String getId() {
        return id;
    }
}
