package pl.librus.client.api;

/**
 * Created by Adam on 16.12.2016.
 */

public class PlainLesson {
    private int id, teacherId, subjectId;

    PlainLesson(int id, int teacherId, int subjectId) {
        this.id = id;
        this.teacherId = teacherId;
        this.subjectId = subjectId;
    }

    public int getId() {
        return id;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public int getSubjectId() {
        return subjectId;
    }
}
