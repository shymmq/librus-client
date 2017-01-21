package pl.librus.client.timetable;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDoneCallback;
import org.jdeferred.android.AndroidExecutionScope;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.items.IFlexible;
import pl.librus.client.LibrusUtils;
import pl.librus.client.R;
import pl.librus.client.api.Lesson;
import pl.librus.client.api.LibrusData;
import pl.librus.client.api.SchoolDay;
import pl.librus.client.api.SchoolWeek;
import pl.librus.client.cache.DataLoader;
import pl.librus.client.cache.LibrusCache;
import pl.librus.client.cache.LibrusCacheLoader;
import pl.librus.client.ui.MainFragment;

import static org.joda.time.DateTimeConstants.MONDAY;
import static pl.librus.client.LibrusConstants.TIMETABLE_CACHE;

public class TimetableFragment extends Fragment implements MainFragment {
    private static final String ARG_DATA = "data";
    private static final String STATE_SCHOOLWEEKS = "TimetableFragment:weeks";
    final ProgressItem progress = new ProgressItem();
    private final String TAG = "librus-client-log";
    LocalDate lastDisplayedWeek = null;
    TimetableAdapter adapter;
    LinearLayoutManager layoutManager;
    LocalDate startDate = LocalDate.now().withDayOfWeek(MONDAY);
    int page = 0;
    private List<IFlexible> listElements = new ArrayList<>();
    private OnSetupCompleteListener listener;
    private RecyclerView recyclerView;
    private View root;
    private View progressBar;

    public static TimetableFragment newInstance() {
        //Bundle args = new Bundle();
        //args.putSerializable(ARG_DATA, data);
        //fragment.setArguments(args);

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
//            public void run() {
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.root = view;

        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_timetable_recycler);
        progressBar = view.findViewById(R.id.fragment_timetable_progress);

        recyclerView.setVisibility(View.VISIBLE);
//        progressBar.setVisibility(View.VISIBLE);

        lastDisplayedWeek = LocalDate.now().withDayOfWeek(MONDAY).minusWeeks(1);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        listElements.clear();
        page = 0;
        adapter = new TimetableAdapter(null);
        adapter.setDisplayHeadersAtStartUp(true);
//        adapter.setDisplayHeadersAtStartUp(true);
        //adapter.setEndlessScrollListener(TimetableFragment.this, progress)

        adapter.setEndlessProgressItem(progress);
//        adapter.mItemClickListener = new FlexibleAdapter.OnItemClickListener() {
//            @Override
//            public boolean onItemClick(int position) {
//
//                LibrusUtils.log("Element " + position + " clicked " + adapter.getItem(position).getClass().getName());
//                return false;
//            }
//        };
        final DoneCallback<SchoolWeek> callback = new AndroidDoneCallback<SchoolWeek>() {
            @Override
            public AndroidExecutionScope getExecutionScope() {
                return null;
            }

            @Override
            public void onDone(final SchoolWeek result) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LibrusUtils.log(result.getWeekStart().toString() + " downloaded");
                        progress.setStatus(ProgressItem.IDLE);
                        List<IFlexible> newElements = getElements(result);
                        adapter.onLoadMoreComplete(newElements);
                        //adapter.addItems(adapter.getItemCount() - 1, newElements);
                        listElements.addAll(newElements);
                        page++;
                        //adapter.updateDataSet(new ArrayList<>(newElements));
                    }
                });
            }
        };
        adapter.onLoadMoreListener = new TimetableAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                progress.setStatus(ProgressItem.LOADING);
                adapter.notifyItemChanged(adapter.getGlobalPositionOf(progress));
                loadMore(page).done(callback);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.onLoadMoreListener.onLoadMore();
        // if (savedInstanceState == null) {
        //first launch
//            final LocalDate weekStart = LocalDate.now().withDayOfWeek(MONDAY);
//            new DataLoader(getContext()).getSchoolWeek(weekStart).done(new DoneCallback<SchoolWeek>() {
//                @Override
//                public void onDone(SchoolWeek result) {
//                    lastDisplayedWeek = weekStart;
//                    listElements.addAll(getElements(result));
//                    finishLoading();
//                }
//            });
//        } else {
//            finishLoading();
//        }
    }

    private Promise<SchoolWeek, Void, Void> loadMore(int currentPage) {
        final LocalDate weekStart = startDate.plusWeeks(currentPage);
        LibrusUtils.log("OnLoadMore\n" +
                "currentPage: " + currentPage + "\n" +
                "weekStart: " + weekStart.toString());

        return new DataLoader(getContext()).getSchoolWeek(weekStart, currentPage == 0);
    }
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putSerializable("list_items", (Serializable) listElements);
//        outState.putSerializable("last_date", lastDisplayedWeek);
//    }

    @Override
    public void refresh(LibrusData cache) {
        Log.d(TAG, "TimetableFragment update()");
    }

    @Override
    public void setOnSetupCompleteListener(OnSetupCompleteListener listener) {
        this.listener = listener;
    }

    List<IFlexible> getElements(SchoolWeek schoolWeek) {
        List<SchoolDay> schoolDays = schoolWeek.getSchoolDays();
        List<IFlexible> res = new ArrayList<>();
        for (SchoolDay schoolDay : schoolDays) {

            LessonHeaderItem headerItem = new LessonHeaderItem(schoolDay.getDate());
            if (schoolDay.getLessons().size() > 0) {

                for (int i = 0; i <= schoolDay.getLastLesson(); i++) {

                    Lesson lesson = schoolDay.getLesson(i);
                    if (lesson != null) {
                        res.add(new LessonItem(headerItem, lesson, getContext()));
                    }
                }

            } else {
                res.add(new EmptyLessonItem(headerItem, schoolDay.getDate()));
            }
        }
        return res;
    }

    Promise<LibrusCache, Void, Void> loadCache(final LocalDate date) {
//        final Deferred<LibrusCache, Void, Void> deferred = new DeferredObject<>();
        LibrusCacheLoader cacheLoader = new LibrusCacheLoader(getContext());
        return cacheLoader.load(TIMETABLE_CACHE);
    }

    void updateList() {
//        adapter.updateDataSet(new ArrayList<>(listElements), true);
//        adapter.hideAllHeaders();
//        adapter.showAllHeaders();
    }
}
