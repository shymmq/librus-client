package pl.librus.client.api;

import java.io.Serializable;

/**
 * Created by szyme on 10.12.2016. librus-client
 */

public class GradeCategory implements Serializable {
    private static final long serialVersionUID = 4317461778423619733L;
    private String id, name;
    private int weight;

    GradeCategory(String id, String name, int weight) {
        this.id = id;
        this.name = name;
        this.weight = weight;
    }

    public String getId() {
        return id;
    }

    public int getWeight() {
        return weight;
    }

    public String getName() {
        return name;
    }
}
