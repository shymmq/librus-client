package pl.librus.client.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import pl.librus.client.datamodel.HasId;

/**
 * Created by szyme on 30.01.2017.
 */

public class Me {
    private LibrusAccount account;
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
