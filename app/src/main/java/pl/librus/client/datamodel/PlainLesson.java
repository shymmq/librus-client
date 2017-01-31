package pl.librus.client.datamodel;

public class PlainLesson extends HasId {
    private HasId teacher;
    private HasId subject;

    public PlainLesson() {
    }

    public PlainLesson(String id, String teacherId, String subjectId) {
        this.setId(id);
        this.teacher = new HasId(teacherId);
        this.subject = new HasId(subjectId);
    }

    public HasId getTeacher() {
        return teacher;
    }

    public HasId getSubject() {
        return subject;
    }
}
