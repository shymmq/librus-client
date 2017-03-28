package pl.librus.client.ui;

import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;

import javax.inject.Inject;

import java8.util.stream.StreamSupport;
import pl.librus.client.MainActivityScope;
import pl.librus.client.MainApplication;
import pl.librus.client.R;
import pl.librus.client.presentation.MainFragmentPresenter;
import pl.librus.client.presentation.SettingsPresenter;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Inject
    SettingsPresenter presenter;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        MainApplication.getMainActivityComponent()
                .inject(this);
        presenter.setFragment(this);
        addPreferencesFromResource(R.xml.preferences);
        updateAvailableFragments();
        addThemeChangeListener();
    }

    private void updateAvailableFragments() {
        ListPreference list = (ListPreference) getPreferenceScreen().findPreference("defaultFragment");

        CharSequence[] labels = StreamSupport.stream(presenter.getFragmentPresenters())
                .map(MainFragmentPresenter::getTitle)
                .map(this::getString)
                .toArray(CharSequence[]::new);
        CharSequence[] values = StreamSupport.stream(presenter.getFragmentPresenters())
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
