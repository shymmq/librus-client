package pl.librus.client.api;

import java.io.Serializable;

/**
 * Created by Adam on 16.12.2016.
 */

public class AttendanceCategory implements Serializable {
    private static final long serialVersionUID = 6194370777524603734L;
    private String id, name, shortName, colorRGB;
    private boolean isStandard, isPresenceKind;
    private int order;

    public AttendanceCategory(String id, String name, String shortName, Boolean isStandard, String colorRGB, boolean isPresenceKind, int order) {
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

    public int getOrder() {
        return order;
    }

    public boolean isPresenceKind() {
        return isPresenceKind;
    }

    public boolean isStandard() {
        return isStandard;
    }

    public String getColorRGB() {
        return colorRGB;
    }
}
