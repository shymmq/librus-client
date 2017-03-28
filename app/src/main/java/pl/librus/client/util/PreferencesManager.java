package pl.librus.client.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.common.base.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by robwys on 28/03/2017.
 */

@Singleton
public class PreferencesManager {
    private final SharedPreferences prefs;

    @Inject
    public PreferencesManager(Context context) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Optional<String> getString(String key) {
        return Optional.fromNullable(prefs.getString(key, null));
    }

    public Optional<String> getLogin() {
        return getString("login");
    }

    public void clearAll() {
        prefs.edit()
                .clear()
                .apply();
    }
}
