package pl.librus.client;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.transition.Fade;
import android.transition.Slide;

/**
 * Created by Adam on 06.11.16
 */

public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("settings_changed", true);
            EditTextPreference lastSyncEdit = (EditTextPreference)findPreference("lastSynchronization");
            lastSyncEdit.setTitle("Ostatnia synchronizacja: " + prefs.getString("lastSynchronization", null));
            editor.commit();
        }
    }
}
