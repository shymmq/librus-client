package pl.librus.client.api;

import java.io.Serializable;

public class Teacher implements Serializable {
    private static final long serialVersionUID = -3067488250588141046L;
    private String id;
    private String firstName;
    private String lastName;

    public Teacher(String id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return firstName != null && lastName != null ? firstName + ' ' + lastName : id;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }
}
