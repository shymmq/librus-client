package pl.librus.client;

import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.Serializable;

/**
 * Created by Adam on 2016-10-31.
 */

public class Announcement implements Serializable {
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
        this.startDate = LocalDate.parse(data.getString("startDate"));
        this.endDate = LocalDate.parse(data.getString("endDate"));
        this.subject = data.getString("Subject");
        this.content = data.getString("Content");
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

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }
}
