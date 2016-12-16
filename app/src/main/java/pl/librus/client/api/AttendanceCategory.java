package pl.librus.client.api;

import android.graphics.Color;

import java.io.Serializable;

/**
 * Created by Adam on 16.12.2016.
 */

public class AttendanceCategory implements Serializable {
    private String id, name, shortName;
    private Boolean isStandard, isPresenceKind;
    private int order, colorRGB;

    public AttendanceCategory(String id, String name, String shortName, Boolean isStandard, Boolean isPresenceKind, int order, int colorRGB) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.isStandard = isStandard;
        this.isPresenceKind = isPresenceKind;
        this.colorRGB = colorRGB;
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

    public Boolean getIsPresenceKind() {
        return isPresenceKind;
    }

    public int getColorRGB() {
        return colorRGB;
    }

    public int getOrder() {
        return order;
    }
}
