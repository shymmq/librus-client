package pl.librus.client.api;

import java.io.Serializable;

/**
 * Created by szyme on 07.12.2016. librus-client
 */
class EventCategory implements Serializable {
    private String name;
    private String id;

    EventCategory(String id, String name) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

}
