package pl.librus.client.api;

import java.io.Serializable;

/**
 * Created by szyme on 07.12.2016. librus-client
 */
public class EventCategory implements Serializable {
    private static final long serialVersionUID = 8913346963576362939L;
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
