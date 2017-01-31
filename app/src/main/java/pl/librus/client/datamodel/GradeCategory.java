package pl.librus.client.datamodel;

public class GradeCategory extends HasId {

    private String name;
    private int weight;

    public GradeCategory() {
    }

    public GradeCategory(String id, String name, int weight) {
        super(id);
        this.name = name;
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public String getName() {
        return name;
    }
}
