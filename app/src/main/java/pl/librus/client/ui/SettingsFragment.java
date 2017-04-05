package pl.librus.client.ui;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

import java.util.List;

import javax.inject.Inject;

import java8.util.stream.StreamSupport;
import pl.librus.client.MainApplication;
import pl.librus.client.R;
import pl.librus.client.presentation.MainFragmentPresenter;
import pl.librus.client.presentation.SettingsPresenter;

public class SettingsFragment extends PreferenceFragmentCompat implements SettingsView,SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    SettingsPresenter presenter;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        MainApplication.getMainActivityComponent()
                .inject(this);
        presenter.attachView(this);
        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager.setDefaultValues(getContext(), R.xml.preferences, false);
        updateAvailableFragments();
        addThemeChangeListener();
    }
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        Resources r = getResources();
        Preference p = findPreference(key);

        if(p instanceof SwitchPreference) {
            if(p.getKey().equals(r.getString(R.string.prefs_enable_notifications_key))) {
                if (!((SwitchPreference) p).isChecked()) {
                    findPreference(r.getString(R.string.prefs_enabled_notification_types_key))
                            .setEnabled(false);
                } else {
                    findPreference(r.getString(R.string.prefs_enabled_notification_types_key))
                            .setEnabled(true);
                }
            }
        }
    }
    @Override
    public void updateAvailableFragments(List<? extends MainFragmentPresenter> presenters) {
        ListPreference list = (ListPreference) getPreferenceScreen().findPreference("defaultFragment");

        CharSequence[] labels = StreamSupport.stream(presenters)
                .map(MainFragmentPresenter::getTitle)
                .map(this::getString)
                .toArray(CharSequence[]::new);
        CharSequence[] values = StreamSupport.stream(presenters)
                .map(MainFragmentPresenter::getTitle)
                .map(String::valueOf)
                .toArray(CharSequence[]::new);

        list.setEntries(labels);
        list.setEntryValues(values);
    }

    private void addThemeChangeListener() {
        SwitchPreference pref = (SwitchPreference) getPreferenceScreen().findPreference("selectTheme");
        pref.setOnPreferenceChangeListener((prev, val) -> {
            getActivity().recreate();
            return true;
        });
    }

}
