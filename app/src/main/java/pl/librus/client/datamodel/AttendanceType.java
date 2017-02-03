package pl.librus.client.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.field.DatabaseField;

/**
 * Created by Adam on 16.12.2016.
 * Class representing /Attendances/Types item
 */

public class AttendanceType {
    @DatabaseField(id = true)
    private String id;
    @DatabaseField
    @JsonProperty("Name")
    private String name;
    @DatabaseField
    @JsonProperty("ColorRGB")
    private String colorRGB;
    @DatabaseField
    @JsonProperty("Short")
    private String shortName;
    @DatabaseField
    @JsonProperty("Standard")
    private boolean standard;
    @DatabaseField
    @JsonProperty("IsPresenceKind")
    private boolean presenceKind;
    @DatabaseField
    @JsonProperty("Order")
    private int order;

    public AttendanceType() {
    }

    public String getName() {
        return name;
    }

    public String getColorRGB() {
        return colorRGB;
    }

    public String getShortName() {
        return shortName;
    }

    public boolean isStandard() {
        return standard;
    }

    public boolean isPresenceKind() {
        return presenceKind;
    }

    public int getOrder() {
        return order;
    }

    public String getId() {
        return id;
    }
}
