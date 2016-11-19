package pl.librus.client;

import org.joda.time.LocalDate;

import java.io.Serializable;

/**
 * Created by Adam on 2016-10-31.
 */

class Announcement implements Serializable {
    private final Integer id;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String subject;
    private final String content;
    private final Teacher teacher;

    public Announcement(int id, LocalDate startDate, LocalDate endDate, String subject, Teacher teacher, String content) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.subject = subject;
        this.teacher = teacher;
        this.content = content;
    }

//    Announcement(JSONObject data) throws JSONException {
//        this.id = data.getInt("Id");
//        this.startDate = LocalDate.parse(data.getString("StartDate"));
//        this.endDate = LocalDate.parse(data.getString("EndDate"));
//        this.subject = data.getString("Subject");
//        this.content = data.getString("Content");
//    }

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
}
