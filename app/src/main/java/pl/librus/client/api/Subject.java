package pl.librus.client.api;

import java.io.Serializable;

public class Subject implements Serializable {
    static final long serialVersionUID = 6430596135265744363L;
    private String name;
    private String id;

    Subject(String id, String name) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
