package pl.librus.client.datamodel;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by szyme on 31.01.2017.
 * A class representing an id value wrapped in a JSONObject; for use with Jackson
 */

public class HasId {

    @DatabaseField(id = true)
    public String id;

    public HasId() {
    }

    public HasId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
