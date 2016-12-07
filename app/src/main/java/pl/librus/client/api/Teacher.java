package pl.librus.client.api;

import java.io.Serializable;

public class Teacher implements Serializable {
    private String id;
    private String firstName;
    private String lastName;

    Teacher(String id, String firstName, String lastName) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getName() {
        return firstName + ' ' + lastName;
    }

}
