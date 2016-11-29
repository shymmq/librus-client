package pl.librus.client.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class LibrusAccount implements Serializable {
    private String id,
            classId,
            firstName,
            lastName,
            login,
            email;

    LibrusAccount(JSONObject data) throws JSONException {
        JSONObject me = data.getJSONObject("Me");
        this.classId = me.getJSONObject("Class").getString("Id");
        JSONObject accountJSON = me.getJSONObject("Account");
        this.id = accountJSON.getString("Id");
        this.firstName = accountJSON.getString("FirstName");
        this.lastName = accountJSON.getString("LastName");
        this.login = accountJSON.getString("Login");
        this.email = accountJSON.getString("Email");
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    public String getId() {
        return id;
    }

    public String getClassId() {
        return classId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }
}
