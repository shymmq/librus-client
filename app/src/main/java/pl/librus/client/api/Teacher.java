package pl.librus.client.api;

import java.io.Serializable;

public class Teacher implements Serializable {
    private static final long serialVersionUID = -3067488250588141046L;
    private String id;
    private String firstName;
    private String lastName;

    Teacher(String id) {
        this.id = id;
    }

    void setName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return firstName != null && lastName != null ? firstName + ' ' + lastName : id;
    }

}
