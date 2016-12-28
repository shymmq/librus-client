package pl.librus.client.api;

import java.io.Serializable;

public class Subject implements Serializable {
    private static final long serialVersionUID = 6430596135265744363L;
    private String name;
    private String id;

    Subject(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
