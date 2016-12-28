package pl.librus.client.timetable;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;
import pl.librus.client.R;
import pl.librus.client.api.Lesson;
import pl.librus.client.api.LibrusData;
import pl.librus.client.api.SchoolDay;
import pl.librus.client.api.SchoolWeek;
import pl.librus.client.ui.MainActivity;
import pl.librus.client.ui.MainFragment;

public class TimetableFragment extends Fragment implements MainFragment {
    private static final String ARG_DATA = "data";
    private final String TAG = "librus-client-log";
    LibrusData data;
    private List<IFlexible> listElements = new ArrayList<>();
    private boolean useTabs = false;
    private OnSetupCompleteListener listener;

    public static TimetableFragment newInstance(LibrusData data) {
        TimetableFragment fragment = new TimetableFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATA, data);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root;

        data = (LibrusData) getArguments().getSerializable("data");
        assert data != null;
        List<SchoolWeek> schoolWeeks = data.getSchoolWeeks();
        Collections.sort(schoolWeeks);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        useTabs = prefs.getBoolean("useTabs", false);

        if (!useTabs) {
            root = inflater.inflate(R.layout.fragment_timetable, container, false);
            final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.fragment_timetable_recycler);

            listElements.clear();

            for (SchoolWeek schoolWeek : schoolWeeks) addSchoolWeek(schoolWeek);

            final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);

            final FlexibleAdapter<IFlexible> adapter = new FlexibleAdapter<>(listElements);
            adapter.setDisplayHeadersAtStartUp(true);
            recyclerView.setAdapter(adapter);

            //scroll to default position after layout is completed
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    layoutManager.scrollToPositionWithOffset(adapter.getGlobalPositionOf(new LessonHeaderItem(LocalDate.now())), 0);
                }
            }, 50);

        } else {
            root = inflater.inflate(R.layout.fragment_timetable_tabs, container, false);

            List<SchoolDay> schoolDays = new ArrayList<>();
            for (SchoolWeek w : data.getSchoolWeeks()) schoolDays.addAll(w.getSchoolDays());
            Collections.sort(schoolDays);
            ViewPager viewPager = (ViewPager) root.findViewById(R.id.fragment_timetable_viewpager);
            ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager(), schoolDays);
            viewPager.setAdapter(adapter);

            TabLayout tabs = (TabLayout) inflater.inflate(R.layout.tabs, null);
            ((MainActivity) getActivity()).addToolbarView(tabs);
            tabs.setupWithViewPager(viewPager);

            viewPager.setCurrentItem(schoolDays.indexOf(new SchoolDay(LocalDate.now())));
        }
        if (listener != null) listener.onSetupComplete();
        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (useTabs)
            ((MainActivity) getActivity()).removeToolbarView();
    }

    void addSchoolWeek(SchoolWeek week) {
        List<SchoolDay> schoolDays = week.getSchoolDays();
        for (SchoolDay schoolDay : schoolDays) {
            LessonHeaderItem headerItem = new LessonHeaderItem(schoolDay.getDate());
            if (schoolDay.getLessons().size() > 0) {

                for (int i = 0; i < schoolDay.getLastLesson(); i++) {

                    Lesson lesson = schoolDay.getLesson(i);
                    if (lesson != null) {
                        listElements.add(new LessonItem(headerItem, lesson, getContext()));
                    }

                }

            } else {
                listElements.add(new EmptyLessonItem(headerItem, schoolDay.getDate()));
            }
        }
    }

    @Override
    public void refresh(LibrusData cache) {
        Log.d(TAG, "TimetableFragment update()");
    }

    @Override
    public void setOnSetupCompleteListener(OnSetupCompleteListener listener) {
        this.listener = listener;
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<SchoolDay> schoolDays;

        ViewPagerAdapter(FragmentManager fm, List<SchoolDay> schoolDays) {
            super(fm);

            this.schoolDays = schoolDays;
        }

        @Override
        public TimetablePageFragment getItem(int position) {
            return TimetablePageFragment.newInstance(schoolDays.get(position));
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TimetableUtils.getTitle(schoolDays.get(position).getDate(), true, false);
        }

        @Override
        public int getCount() {
            return schoolDays.size();
        }
    }
}
