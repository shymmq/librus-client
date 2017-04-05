package pl.librus.client.util;

/**
 * Created by szyme on 18.01.2017.
 * Some useful global constants
 */

public class LibrusConstants {
    //debug
    static final String TAG = "librus-client-log";
    static final boolean DBG = true;

    public static final String REGISTER = "register";

    public static final String ENABLE_NOTIFICATIONS = "enable_notifications";

    public static final String ENABLED_NOTIFICATION_TYPES = "enabled_notification_types";

    public static final String[] NOTIFICATION_TYPES = {
            "announcements",
            "grades",
            "events",
            "lucky_numbers"
    };

}
