package pl.librus.client.timetable;

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

import pl.librus.client.R;
import pl.librus.client.api.Timetable;
import pl.librus.client.ui.MainActivity;

public class TimetableFragment extends Fragment {
    private final String TAG = "librus-client-log";
    private TabLayout tabLayout;

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
        tabLayout = (TabLayout) LayoutInflater.from(getContext()).inflate(R.layout.tabs, null);
        AppBarLayout appBarLayout = ((MainActivity) getActivity()).getAppBarLayout();
        ViewPager viewPager = (ViewPager) root.findViewById(R.id.container);

        Timetable timetable = (Timetable) getArguments().getSerializable("data");

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager(), timetable);

        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setCurrentItem(TimetableUtils.getStartTab(), true);

        tabLayout.setupWithViewPager(viewPager);
        appBarLayout.addView(tabLayout);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) getActivity()).getAppBarLayout().removeView(tabLayout);
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
            return TimetableUtils.getTabTitle(position, false, true);
        }
    }
}
