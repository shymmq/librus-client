package pl.librus.client.api;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by szyme on 07.12.2016. librus-client
 */
@DatabaseTable(tableName = "event_categories")
public class EventCategory {
    @DatabaseField
    private String name;
    @DatabaseField(id = true)
    private String id;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

}
