package pl.librus.client.ui;

import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.google.common.collect.Lists;

import java.util.List;

import java8.util.function.Consumer;
import java8.util.stream.StreamSupport;
import pl.librus.client.R;

public class SettingsFragment extends PreferenceFragmentCompat implements MainFragment {

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        updateAvailableFragments();
        addThemeChangeListener();
    }

    private void updateAvailableFragments() {
        ListPreference list = (ListPreference) getPreferenceScreen().findPreference("defaultFragment");
        List<BaseFragment> fragments = new FragmentsRepository().getAll();

        CharSequence[] labels = StreamSupport.stream(fragments)
                .map(MainFragment::getTitle)
                .map(this::getString)
                .toArray(CharSequence[]::new);
        CharSequence[] values = StreamSupport.stream(fragments)
                .map(MainFragment::getTitle)
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

    @Override
    public void setMenuActionsHandler(Consumer<List<? extends MenuAction>> handler) {
        //ignore
    }

    @Override
    public int getTitle() {
        return R.string.settings_title;
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_settings_black_48dp;
    }
}
