package pl.librus.client.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class LibrusAccount implements Serializable {
    private static final long serialVersionUID = 432189466589507680L;
    private String id,
            firstName,
            lastName,
            login,
            email;

    public LibrusAccount() {
    }

    LibrusAccount(JSONObject data) throws JSONException {
        JSONObject me = data.getJSONObject("MeTable");
        JSONObject accountJSON = me.getJSONObject("MeTable");
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

    @Override
    public String toString() {
        return "LibrusAccount{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", login='" + login + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
