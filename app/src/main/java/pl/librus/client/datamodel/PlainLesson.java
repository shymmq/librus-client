package pl.librus.client.datamodel;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "lessons")
public class PlainLesson {
    @DatabaseField(id = true)
    private String id;
    @DatabaseField
    private HasId teacher;
    @DatabaseField
    private HasId subject;

    public PlainLesson() {
    }

    public PlainLesson(String id, String teacherId, String subjectId) {
        this.id = id;
        this.teacher = new HasId(teacherId);
        this.subject = new HasId(subjectId);
    }

    public String getId() {
        return id;
    }

    public HasId getTeacher() {
        return teacher;
    }

    public HasId getSubject() {
        return subject;
    }
}
