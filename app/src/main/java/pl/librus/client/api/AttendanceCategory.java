package pl.librus.client.api;

import android.graphics.Color;

import java.io.Serializable;

/**
 * Created by Adam on 16.12.2016.
 */

public class AttendanceCategory implements Serializable {
    private String id, name, shortName, colorRGB;
    private Boolean isStandard, isPresenceKind;
    private int order;

    public AttendanceCategory(String id, String name, String shortName, Boolean isStandard, String colorRGB, Boolean isPresenceKind, int order) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.isStandard = isStandard;
        this.colorRGB = colorRGB;
        this.isPresenceKind = isPresenceKind;
        this.order = order;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public Boolean getIsStandard() {
        return isStandard;
    }

    public String getColorRGB() {
        return colorRGB;
    }

    public Boolean getIsPresenceKind() {
        return isPresenceKind;
    }

    public int getOrder() {
        return order;
    }
}
