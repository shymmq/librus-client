package pl.librus.client;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TimetableFragment extends Fragment {
    private final String TAG = "librus-client-log";
    private ViewPager viewPager;

    public static TimetableFragment newInstance(Timetable timetable) {

        Bundle args = new Bundle();
        args.putSerializable("data", timetable);
        TimetableFragment fragment = new TimetableFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_timetable, container, false);
        viewPager = (ViewPager) root.findViewById(R.id.container);
        Timetable timetable = (Timetable) getArguments().getSerializable("data");
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager(), timetable);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setCurrentItem(TimetableUtils.getStartTab(), true);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        TabLayout tabLayout = (TabLayout) LayoutInflater.from(getContext()).inflate(R.layout.tabs, null);
        AppBarLayout appBarLayout = ((MainActivity) getActivity()).getAppBarLayout();
        appBarLayout.addView(tabLayout, 1);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).getAppBarLayout().removeViewAt(1);
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
            return TimetableUtils.getDayCount();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TimetableUtils.getTabTitle(position, true, true);
        }
    }
}
