package pl.librus.client;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Adam on 2016-10-31.
 */

class Announcement implements Serializable {
    private Integer id;
    private LocalDate startDate, endDate;
    private String subject, content;
    private Teacher teacher;

    public Announcement(int id, LocalDate startDate, LocalDate endDate, String subject, String content) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.subject = subject;
        this.content = content;
    }

    Announcement(JSONObject data) throws JSONException {
        this.id = data.getInt("Id");
        this.startDate = LocalDate.parse(data.getString("StartDate"));
        this.endDate = LocalDate.parse(data.getString("EndDate"));
        this.subject = data.getString("Subject");
        this.content = data.getString("Content");
        this.teacher = new Teacher(data.getJSONObject("AddedBy").getString("Id"), "//TODO", "Dodać imię i nazwisko");
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

    String getSubject() {
        return subject;
    }

    String getContent() {
        return content;
    }
}
