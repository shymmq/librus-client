package pl.librus.client.api;

import java.io.Serializable;

/**
 * Created by szyme on 12.12.2016. librus-client
 */

public class GradeComment implements Serializable {

    private static final long serialVersionUID = 2612248327846363472L;

    private String id, addedById, gradeId, text;

    public GradeComment(String id, String addedById, String gradeId, String text) {
        this.id = id;
        this.addedById = addedById;
        this.gradeId = gradeId;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    String getAddedById() {
        return addedById;
    }

    String getGradeId() {
        return gradeId;
    }

    public String getText() {
        return text;
    }
}
