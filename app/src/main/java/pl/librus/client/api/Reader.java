package pl.librus.client.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Set;

import pl.librus.client.datamodel.Identifiable;

/**
 * Created by szyme on 16.12.2016. librus-client
 */

public class Reader {
    private final Context context;

    public Reader(Context c) {
        this.context = c;
    }

    public boolean isRead(Identifiable object) {
        return getRead(object.getClass())
                .contains(object.getId());
    }

    public void read(Identifiable object) {
        modify(object, true);
    }

    private void modify(Identifiable object, boolean mode) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> read = Sets.newHashSet(getRead(object.getClass()));
        if (mode) read.add(object.getId());
        else read.remove(object.getId());
        editor.putStringSet(object.getClass().getCanonicalName(), read);
        editor.apply();
    }

    private Set<String> getRead(Class clazz) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getStringSet(clazz.getCanonicalName(), new HashSet<String>());

    }
}
