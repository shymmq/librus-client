package pl.librus.client.datamodel;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by szyme on 12.12.2016. librus-client
 */
@DatabaseTable(tableName = "grade_comments")
public class GradeComment {
    public static final String COLUMN_NAME_GRADE_ID = "grade";

    @DatabaseField(id = true)
    private String id;
    @DatabaseField
    private HasId addedBy;
    @DatabaseField(columnName = COLUMN_NAME_GRADE_ID)
    private HasId grade;
    @DatabaseField
    private String text;

    public GradeComment() {
    }

    public String getId() {
        return id;
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
