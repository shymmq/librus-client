package pl.librus.client;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TimetableFragment extends Fragment {
    private final String TAG = "librus-client-log";

    public static TimetableFragment newInstance(Timetable timetable) {

        Bundle args = new Bundle();
        args.putSerializable("data", timetable);
        TimetableFragment fragment = new TimetableFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_timetable, container, false);
        ViewPager viewPager = (ViewPager) root.findViewById(R.id.container);
        TabLayout tabLayout = (TabLayout) root.findViewById(R.id.tabs);
        Timetable timetable = (Timetable) getArguments().getSerializable("data");
        assert timetable != null;
        Log.d(TAG, "onCreateView: timetable: " + timetable.getTimetable().toString());
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager(), timetable);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(0, true);

//        log("Tab count : " + TimetableUtils.getDayCount());
//        log("Start date : " + TimetableUtils.getStartDate());
//        log("Week start : " + TimetableUtils.getWeekStart());
        return root;
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final Timetable timetable;

        SectionsPagerAdapter(FragmentManager fm, Timetable timetable) {
            super(fm);
            this.timetable = timetable;
        }

        @Override
        public Fragment getItem(int position) {
            return TimetablePageFragment.newInstance(timetable.getSchoolDay(TimetableUtils.getTabDate(position)));
        }

        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TimetableUtils.getTabTitle(position, true, true);
        }
    }
}
