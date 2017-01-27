package pl.librus.client.api;

import java.io.Serializable;

public class Subject implements Serializable {
    private static final long serialVersionUID = 6430596135265744363L;
    private String name;
    private String id;

    public Subject(String id, String name) {
        this.name = name;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subject subject = (Subject) o;

        return id.equals(subject.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }


}
