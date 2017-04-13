package pl.librus.client.ui;

import android.os.Bundle;
import android.support.v14.preference.MultiSelectListPreference;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import java8.util.J8Arrays;
import java8.util.stream.StreamSupport;
import pl.librus.client.MainApplication;
import pl.librus.client.R;
import pl.librus.client.presentation.MainFragmentPresenter;
import pl.librus.client.presentation.SettingsPresenter;
import pl.librus.client.util.LibrusConstants;

public class SettingsFragment extends PreferenceFragmentCompat implements SettingsView {

    @Inject
    SettingsPresenter presenter;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        MainApplication.getMainActivityComponent()
                .inject(this);
        addPreferencesFromResource(R.xml.preferences);
        addThemeChangeListener();
        addOnEnableNotificationsChangeListener();
        presenter.attachView(this);
        PreferenceManager.setDefaultValues(getContext(), R.xml.preferences, false);
    }


    @Override
    public void updateAvailableNotifications() {
        MultiSelectListPreference list = (MultiSelectListPreference) getPreferenceScreen().findPreference(LibrusConstants.ENABLED_NOTIFICATION_TYPES);
        String[] labels = J8Arrays.stream(LibrusConstants.NOTIFICATION_TYPES)
                .map(type -> getResources().getIdentifier(type, "string", getContext().getPackageName()))
                .map(getResources()::getString)
                .toArray(String[]::new);

        Set<String> values = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getStringSet(LibrusConstants.ENABLED_NOTIFICATION_TYPES, Sets.newHashSet(LibrusConstants.NOTIFICATION_TYPES));
        list.setValues(values);
        list.setEntryValues(LibrusConstants.NOTIFICATION_TYPES);
        list.setEntries(labels);
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

    private void addOnEnableNotificationsChangeListener() {
        SwitchPreference enableNotifications = (SwitchPreference) getPreferenceScreen().findPreference("enable_notifications");
        Preference notificationTypes = getPreferenceScreen().findPreference("enabled_notification_types");
        enableNotifications.setOnPreferenceChangeListener((preference, newValue) -> {
            notificationTypes.setEnabled((Boolean) newValue);
            return true;
        });
    }

}
