package pl.librus.client;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

class Subject implements Serializable {
    private String name;

    public Subject(String name) {
        this.name = name;
    }

    Subject(JSONObject data) throws JSONException {
        this.name = data.getString("Name");
    }

    public String getName() {
        return name;
    }
}
