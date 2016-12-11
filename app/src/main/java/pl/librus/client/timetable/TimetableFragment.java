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

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.prefs.PreferenceChangeEvent;

import pl.librus.client.R;
import pl.librus.client.api.Event;
import pl.librus.client.api.EventCategory;
import pl.librus.client.api.Lesson;
import pl.librus.client.api.LibrusData;
import pl.librus.client.api.Timetable;
import pl.librus.client.ui.MainActivity;
import pl.librus.client.ui.MainFragment;

public class TimetableFragment extends MainFragment {
    private final String TAG = "librus-client-log";
    private TabLayout tabLayout;
    private boolean displayDates, useRelativeTabNames;

    public static TimetableFragment newInstance(LibrusData cache) {

        Timetable timetable = cache.getTimetable();
        List<Event> events = cache.getEvents();

        for (Event event : events) {
            Lesson lesson = timetable.getLesson(event.getDate(), event.getLessonNumber());
            if (lesson != null) {
                lesson.setEvent(event);
            }
        }

        TimetableFragment fragment = new TimetableFragment();
        Bundle args = new Bundle();
        args.putSerializable("data", timetable);
        args.putSerializable("event_map", (Serializable) cache.getEventCategoriesMap());
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

        Timetable timetable = (Timetable) getArguments().getSerializable("data");

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager(), timetable, (Map<String, EventCategory>) getArguments().getSerializable("event_map"));

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

        private final Timetable timetable;
        private final Map<String, EventCategory> eventCategoryMap;

        SectionsPagerAdapter(FragmentManager fm, Timetable timetable, Map<String, EventCategory> eventCategoryMap) {
            super(fm);
            this.timetable = timetable;
            this.eventCategoryMap = eventCategoryMap;
        }

        @Override
        public Fragment getItem(int position) {
            return TimetablePageFragment.newInstance(timetable.getSchoolDay(TimetableUtils.getTabDate(position)), eventCategoryMap);
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
