package pl.librus.client.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import pl.librus.client.UserScope;
import pl.librus.client.domain.Identifiable;
import pl.librus.client.util.LibrusUtils;

/**
 * Created by szyme on 16.12.2016. librus-client
 */

@UserScope
public class Reader {
    private final Context context;

    @Inject
    public Reader(Context c) {
        this.context = c;
    }

    public boolean isRead(Identifiable object) {
        String classId = getClassId(object);
        return getRead(classId)
                .contains(object.id());
    }

    public void read(Identifiable object) {
        modify(object, true);
    }

    public void modify(Identifiable object, boolean mode) {
        String classId = getClassId(object);
        LibrusUtils.log("Modifying read status create %s:%s to %b", classId, object.id(), mode);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> read = Sets.newHashSet(getRead(classId));
        if (mode) read.add(object.id());
        else read.remove(object.id());
        editor.putStringSet(classId, read);
        editor.apply();
    }

    private Set<String> getRead(String classId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getStringSet(classId, new HashSet<>());

    }

    private String getClassId(Identifiable identifiable) {
        Class<?> clazz = identifiable.getClass();
        while(!clazz.getSuperclass().equals(Object.class)){
            clazz = clazz.getSuperclass();
        }
        return clazz.getSimpleName();
    }
}
