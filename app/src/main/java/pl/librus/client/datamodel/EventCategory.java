package pl.librus.client.datamodel;

import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;

/**
 * Created by szyme on 07.12.2016. librus-client
 */
@Entity
public class EventCategory implements Persistable{
    public String name;

    @Key
    public String id;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

}
