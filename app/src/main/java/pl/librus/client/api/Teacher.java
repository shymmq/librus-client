package pl.librus.client.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Teacher implements Serializable {
    private String name;

    Teacher(JSONObject data) throws JSONException {
        this.name = data.getString("FirstName") + " " + data.getString("LastName");
    }

    public Teacher(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
