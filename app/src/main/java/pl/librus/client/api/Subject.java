package pl.librus.client.api;

import java.io.Serializable;

public class Subject implements Serializable {
    private static final long serialVersionUID = 6430596135265744363L;
    private String name;
    private String id;

    Subject(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }
}
