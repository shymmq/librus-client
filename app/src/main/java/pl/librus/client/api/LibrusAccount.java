package pl.librus.client.api;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "accounts")
public class LibrusAccount {
    @DatabaseField
    private String id;

    @DatabaseField
    private String firstName;

    @DatabaseField
    private String lastName;

    @DatabaseField(id = true)
    private String login;

    @DatabaseField
    private String email;

    public LibrusAccount() {
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
