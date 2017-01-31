package pl.librus.client.datamodel;

/**
 * Created by szyme on 12.12.2016. librus-client
 */

public class GradeComment extends HasId {

    private HasId addedBy, grade;
    private String text;

    public GradeComment() {
    }

    public GradeComment(String id, String addedById, String gradeId, String text) {
        super(id);
        this.addedBy = new HasId(addedById);
        this.grade = new HasId(gradeId);
        this.text = text;
    }

    public HasId getAddedBy() {
        return addedBy;
    }

    public HasId getGrade() {
        return grade;
    }

    public String getText() {
        return text;
    }
}
