package pl.librus.client.datamodel;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "grade_categories")
public class GradeCategory {
    @DatabaseField(id = true)
    private String id;
    @DatabaseField
    private String name;
    @DatabaseField
    private int weight;

    public GradeCategory() {
    }

    public GradeCategory(String id, String name, int weight) {
        this.id = id;
        this.name = name;
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
