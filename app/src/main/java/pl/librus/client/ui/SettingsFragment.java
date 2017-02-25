package pl.librus.client.ui;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

import java.util.List;

import java8.util.stream.StreamSupport;
import pl.librus.client.R;
import pl.librus.client.timetable.TimetableFragment;

public class SettingsFragment extends PreferenceFragment {


    public SettingsFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        ListPreference list = (ListPreference) getPreferenceScreen().findPreference("defaultFragment");
        List<MainFragment> fragments = new FragmentsRepository().getAll();

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

}
