package pl.librus.client.timetable;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import pl.librus.client.R;
import pl.librus.client.api.Event;
import pl.librus.client.api.Lesson;
import pl.librus.client.api.LibrusData;
import pl.librus.client.api.Timetable;
import pl.librus.client.ui.MainActivity;
import pl.librus.client.ui.MainFragment;

public class TimetableFragment extends MainFragment {
    private static final String ARG_DATA = "data";
    private final String TAG = "librus-client-log";
    private TabLayout tabLayout;
    private boolean displayDates, useRelativeTabNames;

    public static TimetableFragment newInstance(LibrusData data) {
        TimetableFragment fragment = new TimetableFragment();
        Bundle args = new Bundle();
//        args.putSerializable("data", timetable);
//        args.putSerializable("event_map", (Serializable) data.getEventCategoriesMap());
        args.putSerializable(ARG_DATA, data);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_timetable, container, false);
        tabLayout = (TabLayout) LayoutInflater.from(getContext()).inflate(R.layout.tabs, null);
        MainActivity activity = (MainActivity) getActivity();
        ViewPager viewPager = (ViewPager) root.findViewById(R.id.container);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        displayDates = sp.getBoolean("displayDates", true);
        useRelativeTabNames = sp.getBoolean("useRelativeTabNames", true);
        Log.d(TAG, "displayDates: " + displayDates);
        Log.d(TAG, "useRelativeTabNames: " + useRelativeTabNames);

        LibrusData data = (LibrusData) getArguments().getSerializable("data");

        assert data != null;
        List<Event> events = data.getEvents();
        Timetable timetable = data.getTimetable();
        for (Event event : events) {
            if (event.getLessonNumber() >= 0) {
                Lesson lesson = timetable.getLesson(event.getDate(), event.getLessonNumber());
                if (lesson != null) {
                    data.getTimetable().getLesson(event.getDate(), event.getLessonNumber()).setEvent(event);
                }
            }
        }

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager(), data);

        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setCurrentItem(TimetableUtils.getDefaultTab(), true);

        tabLayout.setupWithViewPager(viewPager);
        activity.addTabs(tabLayout);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) getActivity()).removeTabs(tabLayout);
    }

    @Override
    public void refresh(LibrusData cache) {
        Log.d(TAG, "TimetableFragment update()");
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        LibrusData data;

        SectionsPagerAdapter(FragmentManager fm, LibrusData data) {
            super(fm);
            this.data = data;
        }

        @Override
        public Fragment getItem(int position) {
            return TimetablePageFragment.newInstance(data, TimetableUtils.getTabDate(position));
        }

        @Override
        public int getCount() {
            return TimetableUtils.getDayCount();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TimetableUtils.getTabTitle(position, displayDates, useRelativeTabNames);
        }
    }
}
