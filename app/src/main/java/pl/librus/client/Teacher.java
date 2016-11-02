package pl.librus.client;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

class Teacher implements Serializable {
    private Integer id;
    private String firstName, lastName;

    public Teacher(Integer id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    Teacher(JSONObject data) throws JSONException {
        this.id = data.getInt("Id");
        this.firstName = data.getString("FirstName");
        this.lastName = data.getString("LastName");
    }

    public Integer getId() {
        return id;
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
