package pl.librus.client;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

class Teacher implements Serializable {
    private String firstName, lastName;

    public Teacher(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    Teacher(JSONObject data) throws JSONException {
        this.firstName = data.getString("FirstName");
        this.lastName = data.getString("LastName");
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getName() {
        return firstName + " " + lastName;
    }
}
