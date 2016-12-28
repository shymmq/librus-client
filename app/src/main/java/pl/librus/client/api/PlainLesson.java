package pl.librus.client.api;

import java.io.Serializable;

/**
 * Created by Adam on 16.12.2016.
 */

class PlainLesson implements Serializable {
    private static final long serialVersionUID = -7695486585587614442L;
    private String id, teacherId, subjectId;

    PlainLesson(String id, String teacherId, String subjectId) {
        this.id = id;
        this.teacherId = teacherId;
        this.subjectId = subjectId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }
}
