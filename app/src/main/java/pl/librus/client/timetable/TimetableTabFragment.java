package pl.librus.client.timetable;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.List;

import pl.librus.client.R;
import pl.librus.client.ui.MainFragment;

/**
 * Created by szyme on 29.01.2017.
 */

public class TimetableTabFragment extends Fragment implements MainFragment {

    private final List<LocalDate> weekStarts = TimetableUtils.getNextFullWeekStarts(LocalDate.now());
    private final LocalDate firstWeekStart = TimetableUtils.getFirstFullWeekStart(LocalDate.now());
    private ViewPager viewPager;

    public TimetableTabFragment() {
    }

    public static TimetableTabFragment newInstance() {
        return new TimetableTabFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timetable_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        viewPager = (ViewPager) view.findViewById(R.id.fragment_timetable_tab_viewpager);
        TabAdapter adapter = new TabAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
    }

    @Override
    public void refresh() {

    }

    private class TabAdapter extends FragmentStatePagerAdapter {

        public TabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return TimetablePageFragment.newInstance(firstWeekStart.plusDays(position));
        }

        @Override
        public int getCount() {
            return weekStarts.size() * DateTimeConstants.DAYS_PER_WEEK;
        }
    }
}
