package pl.librus.client.datamodel;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "teachers")
public class Teacher extends HasId {
    @DatabaseField
    private String firstName;
    @DatabaseField
    private String lastName;
    @DatabaseField
    private boolean isSchoolAdministrator;

    public Teacher() {
    }

    public Teacher(String id, String firstName, String lastName) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return firstName != null && lastName != null ? firstName + ' ' + lastName : id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public boolean getIsSchoolAdministrator() {
        return isSchoolAdministrator;
    }

    public void setIsSchoolAdministrator(boolean schoolAdministrator) {
        isSchoolAdministrator = schoolAdministrator;
    }
}
