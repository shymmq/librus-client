package pl.librus.client.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by szyme on 16.12.2016. librus-client
 */

public class Reader {
    static public final String TYPE_ANNOUNCEMENT = "ReadAnnouncements";
    static public final String TYPE_GRADE = "ReadGrades";

    public static boolean isRead(String type, String id, Context c) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        Set<String> read = prefs.getStringSet(type, new HashSet<String>());
        return read.contains(id);
    }

    public static void read(String type, String id, Context c) {
        modify(type, id, true, c);
    }

    static void forget(String type, String id, Context c) {
        modify(type, id, false, c);
    }

    static private void modify(String type, String id, boolean mode, Context c) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> read = prefs.getStringSet(type, new HashSet<String>());
        editor.remove(type);
        editor.apply();
        if (mode) read.add(id);
        else read.remove(id);
        editor.putStringSet(type, read);
        editor.commit();
    }
}
