package pl.librus.client.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Adam on 16.12.2016.
 */

public class AttendanceType extends HasId {
    @JsonProperty("Name")
    private String name;
    @JsonProperty("ColorRGB")
    private String colorRGB;
    @JsonProperty("Short")
    private String shortName;
    @JsonProperty("Standard")
    private boolean standard;
    @JsonProperty("IsPresenceKind")
    private boolean presenceKind;
    @JsonProperty("Order")
    private int order;

    public AttendanceType() {
    }

    public AttendanceType(String id, String name, String colorRGB, String shortName, boolean standard, boolean presenceKind, int order) {
        super(id);
        this.name = name;
        this.colorRGB = colorRGB;
        this.shortName = shortName;
        this.standard = standard;
        this.presenceKind = presenceKind;
        this.order = order;
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
}
