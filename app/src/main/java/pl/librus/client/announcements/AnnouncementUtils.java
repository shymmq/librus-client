package pl.librus.client.announcements;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by szyme on 06.12.2016.
 */

public class AnnouncementUtils {
    private static final String READ_KEY = "AnnouncementUtils:read";

    public static void markAsRead(String id, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> read = preferences.getStringSet(READ_KEY, new HashSet<String>());
        editor.remove(READ_KEY);
        editor.commit();
        read.add(id);
        editor.putStringSet(READ_KEY, read);
        editor.commit();
    }

    public static Set<String> getRead(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getStringSet(READ_KEY, new HashSet<String>());
    }

    public static void markAsUnread(String id, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> read = preferences.getStringSet(READ_KEY, new HashSet<String>());
        editor.remove(READ_KEY);
        editor.commit();
        read.remove(id);
        editor.putStringSet(READ_KEY, read);
        editor.commit();
    }

}
