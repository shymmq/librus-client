package pl.librus.client.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by szyme on 30.01.2017.
 * Class representing /Me endpoint
 */
@DatabaseTable(tableName = "me")
public class Me {
    @DatabaseField(foreign = true)
    private LibrusAccount account;

    @DatabaseField
    @JsonProperty("Class")
    private HasId librusClass;

    public LibrusAccount getAccount() {
        return account;
    }

    public void setAccount(LibrusAccount account) {
        this.account = account;
    }

    public HasId getLibrusClass() {
        return librusClass;
    }

    @Override
    public String toString() {
        return "MeTable{" +
                "account=" + account +
                ", librusClass=" + librusClass +
                '}';
    }
}
