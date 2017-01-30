package pl.librus.client.timetable;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jdeferred.DoneCallback;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.davidea.flexibleadapter.items.IFlexible;
import pl.librus.client.R;
import pl.librus.client.api.Lesson;
import pl.librus.client.api.LibrusUpdateService;
import pl.librus.client.api.SchoolDay;
import pl.librus.client.api.SchoolWeek;
import pl.librus.client.sql.LibrusDbHelper;
import pl.librus.client.ui.MainFragment;

public class TimetableFragment extends Fragment implements MainFragment {
    final ProgressItem progressItem = new ProgressItem();
    public Runnable onSetupCompleted = new Runnable() {
        @Override
        public void run() {

        }
    };
    TimetableAdapter adapter;
    LinearLayoutManager layoutManager;
    LocalDate startDate;
    int page = 0;
    private OnSetupCompleteListener listener;

    public static TimetableFragment newInstance() {
        return new TimetableFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        LibrusCacheLoader cacheLoader = new LibrusCacheLoader(getContext());
//        final LocalDate weekStart = LocalDate.now().withDayOfWeek(MONDAY);
//        cacheLoader.load(TimetableUtils.getFilenameForDate(weekStart)).done(new DoneCallback<LibrusCache>() {
//            @Override
//            public void onDone(LibrusCache result) {
//                SchoolWeek w = ((TimetableCache) result).getSchoolWeek();
//                addSchoolWeek(w);
//            }
//        }).fail(new FailCallback<String>() {
//            @Override
//            public void onFail(String result) {
//                APIClient client = new APIClient(getContext());
//                client.getSchoolWeek(weekStart).done(new DoneCallback<SchoolWeek>() {
//                    @Override
//                    public void onDone(SchoolWeek result) {
//                        addSchoolWeek(result);
//                    }
//                });
//            }
//        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        //boolean useTabs = prefs.getBoolean("useTabs", false);

        //if (!useTabs) {

        //scroll to default position after layout is completed
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void onUpdateComplete() {
//                layoutManager.scrollToPositionWithOffset(adapter.getGlobalPositionOf(new LessonHeaderItem(LocalDate.now())), 0);
//            }
//        }, 50);

        /*} else {
            TabLayout tabs = (TabLayout) inflater.inflate(R.layout.tabs, null);
            ((MainActivity) getActivity()).addToolbarView(tabs);

            root = inflater.inflate(R.layout.fragment_timetable_tabs, container, false);

            List<SchoolDay> schoolDays = new ArrayList<>();
            for (SchoolWeek w : data.getSchoolWeeks()) schoolDays.addAll(w.getSchoolDays());
            Collections.sort(schoolDays);
            ViewPager viewPager = (ViewPager) root.findViewById(R.id.fragment_timetable_viewpager);
            ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager(), schoolDays);
            viewPager.setAdapter(adapter);

            tabs.setupWithViewPager(viewPager);

            viewPager.setCurrentItem(schoolDays.indexOf(new SchoolDay(LocalDate.now())));
        }
        */
        return inflater.inflate(R.layout.fragment_timetable, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_timetable_recycler);

        recyclerView.setVisibility(View.VISIBLE);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        final LibrusDbHelper dbHelper = new LibrusDbHelper(getContext());

        startDate = TimetableUtils.getLastFullWeekStart(LocalDate.now()).plusWeeks(1);
        List<IFlexible> initialElements = new ArrayList<>();
        List<LocalDate> initialWeekStarts = TimetableUtils.getNextFullWeekStarts(LocalDate.now());

        for (LocalDate weekStart : initialWeekStarts) {
            for (LocalDate date = weekStart; date.isBefore(weekStart.plusWeeks(1)); date = date.plusDays(1)) {

                LessonHeaderItem header = new LessonHeaderItem(date);
                List<Lesson> lessons = dbHelper.getLessonsForDate(date);
                if (lessons == null) {
                    initialElements.add(new EmptyLessonItem(header, date));
                } else {
                    for (Lesson l : lessons) {
                        LessonItem lessonItem = new LessonItem(header, l, getContext());
                        initialElements.add(lessonItem);
                    }
                }
            }
        }

        adapter = new TimetableAdapter(initialElements);

        adapter.setDisplayHeadersAtStartUp(true);
        adapter.setEndlessProgressItem(progressItem);

        adapter.onLoadMoreListener = new TimetableAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                final List<IFlexible> newElements = new ArrayList<>();
                progressItem.setStatus(ProgressItem.LOADING);
                adapter.notifyItemChanged(adapter.getGlobalPositionOf(progressItem));

                //additional pages, load from server
                new LibrusUpdateService(getContext()).getSchoolWeek(startDate.plusWeeks(page)).done(new DoneCallback<SchoolWeek>() {
                    @Override
                    public void onDone(SchoolWeek result) {
                        for (SchoolDay schoolDay : result.getSchoolDays()) {
                            LocalDate date = schoolDay.getDate();
                            LessonHeaderItem header = new LessonHeaderItem(date);
                            if (schoolDay.isEmpty()) {
                                newElements.add(new EmptyLessonItem(header, date));
                            } else {
                                List<Lesson> lessons = new ArrayList<>(schoolDay.getLessons().values());
                                Collections.sort(lessons);
                                for (Lesson l : lessons) {
                                    LessonItem lessonItem = new LessonItem(header, l, getContext());
                                    newElements.add(lessonItem);
                                }
                            }
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressItem.setStatus(ProgressItem.IDLE);
                                adapter.onLoadMoreComplete(newElements);
                                onSetupCompleted.run();
                                page++;
                            }
                        });
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);

        if (listener != null) listener.run();
    }

    @Override
    public void refresh() {
    }

    @Override
    public void setOnSetupCompleteLister(OnSetupCompleteListener listener) {
        this.listener = listener;
    }

    @Override
    public void removeListener() {
        this.listener = null;
    }
}
