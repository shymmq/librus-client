package pl.librus.client.timetable;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jdeferred.Promise;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.items.IFlexible;
import pl.librus.client.LibrusUtils;
import pl.librus.client.R;
import pl.librus.client.api.Lesson;
import pl.librus.client.api.LibrusData;
import pl.librus.client.api.SchoolDay;
import pl.librus.client.api.SchoolWeek;
import pl.librus.client.api.Subject;
import pl.librus.client.api.Teacher;
import pl.librus.client.cache.SchoolWeekLoader;
import pl.librus.client.sql.LibrusDbContract;
import pl.librus.client.sql.LibrusDbHelper;
import pl.librus.client.ui.MainFragment;

import static org.joda.time.DateTimeConstants.MONDAY;

public class TimetableFragment extends Fragment implements MainFragment {
    final ProgressItem progressItem = new ProgressItem();
    TimetableAdapter adapter;
    LinearLayoutManager layoutManager;
    LocalDate startDate = LocalDate.now().withDayOfWeek(MONDAY);
    int page = 0;

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

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_timetable_recycler);

        recyclerView.setVisibility(View.VISIBLE);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new TimetableAdapter(null);
        adapter.setDisplayHeadersAtStartUp(true);
        page = 0;
        adapter.setEndlessProgressItem(progressItem);

        LibrusDbHelper dbHelper = new LibrusDbHelper(getContext());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        adapter.onLoadMoreListener = new TimetableAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                List<IFlexible> newElements = new ArrayList<>();
                progressItem.setStatus(ProgressItem.LOADING);
                adapter.notifyItemChanged(adapter.getGlobalPositionOf(progressItem));
                final LocalDate weekStart = startDate.plusWeeks(page);

                for (LocalDate date = weekStart; date.isBefore(weekStart.plusWeeks(1)); date = date.plusDays(1)) {
                    final long dateMillis = date.toDateTimeAtStartOfDay().getMillis();
                    Cursor cursor = db.query(
                            LibrusDbContract.LessonsTable.TABLE_NAME,
                            null,
                            LibrusDbContract.LessonsTable.COLUMN_NAME_DATE + " = " + dateMillis,
                            null,
                            null,
                            null,
                            LibrusDbContract.LessonsTable.COLUMN_NAME_LESSON_NUMBER
                    );
                    int idIndex = cursor.getColumnIndexOrThrow(LibrusDbContract.LessonsTable.COLUMN_NAME_ID);
                    int lessonNumberIndex = cursor.getColumnIndexOrThrow(LibrusDbContract.LessonsTable.COLUMN_NAME_LESSON_NUMBER);
                    int subjectNameIndex = cursor.getColumnIndexOrThrow(LibrusDbContract.LessonsTable.COLUMN_NAME_SUBJECT_NAME);
                    int subjectIdIndex = cursor.getColumnIndexOrThrow(LibrusDbContract.LessonsTable.COLUMN_NAME_SUBJECT_ID);
                    int teacherIdIndex = cursor.getColumnIndexOrThrow(LibrusDbContract.LessonsTable.COLUMN_NAME_TEACHER_ID);
                    int teacherFirstNameIndex = cursor.getColumnIndexOrThrow(LibrusDbContract.LessonsTable.COLUMN_NAME_TEACHER_FIRST_NAME);
                    int teacherLastNameIndex = cursor.getColumnIndexOrThrow(LibrusDbContract.LessonsTable.COLUMN_NAME_TEACHER_LAST_NAME);
                    LessonHeaderItem header = new LessonHeaderItem(date);
                    while (cursor.moveToNext()) {
                        int lessonNumber = cursor.getInt(lessonNumberIndex);
                        Lesson lesson = new Lesson(
                                cursor.getString(idIndex),
                                lessonNumber,
                                date,
                                LocalTime.now(), LocalTime.now(),
                                new Subject(
                                        cursor.getString(subjectIdIndex),
                                        cursor.getString(subjectNameIndex)),
                                new Teacher(
                                        cursor.getString(teacherIdIndex),
                                        cursor.getString(teacherFirstNameIndex),
                                        cursor.getString(teacherLastNameIndex))
                        );
                        LessonItem lessonItem = new LessonItem(header, lesson, getContext());
                        newElements.add(lessonItem);
                    }
                    cursor.close();
                }
                progressItem.setStatus(ProgressItem.IDLE);
                adapter.onLoadMoreComplete(newElements);
                page++;
//                loadMore(page)
//                        //when data is loaded from cache
//                        .progress(new ProgressCallback<SchoolWeek>() {
//                            @Override
//                            public void onProgress(final SchoolWeek progress) {
//                                //if cached data was found, display on the ui thread
//                                if (progress != null) {
//                                    getActivity().runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            LibrusUtils.log(progress.getWeekStart().toString() + " loaded from cache");
//                                            progressItem.setStatus(ProgressItem.IDLE);
//                                            List<IFlexible> newElements = getElements(progress);
//                                            adapter.onLoadMoreComplete(newElements);
//                                            page++;
//                                        }
//                                    });
//                                }
//                            }
//                        })
//                        //if no cached data was found, display it here
//                        .fail(new FailCallback<SchoolWeek>() {
//                            @Override
//                            public void onFail(final SchoolWeek result) {
//                                getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        LibrusUtils.log(result.getWeekStart().toString() + " downloaded");
//                                        progressItem.setStatus(ProgressItem.IDLE);
//                                        List<IFlexible> newElements = getElements(result);
//                                        adapter.onLoadMoreComplete(newElements);
//                                        page++;
//                                    }
//                                });
//
//                            }
//                        })
//                        //if items were previously loaded from cache, only update the items here.
//                        .done(new DoneCallback<SchoolWeek>() {
//                            @Override
//                            public void onDone(SchoolWeek result) {
//                                LibrusUtils.log(result.getWeekStart().toString() + " updated");
//                            }
//                        });
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.onLoadMoreListener.onLoadMore();
    }

    private Promise<SchoolWeek, SchoolWeek, SchoolWeek> loadMore(int currentPage) {
        final LocalDate weekStart = startDate.plusWeeks(currentPage);
        LibrusUtils.log("OnLoadMore\n" +
                "currentPage: " + currentPage + "\n" +
                "weekStart: " + weekStart.toString());
        return new SchoolWeekLoader(getContext()).hybridLoad(weekStart);
    }

    @Override
    public void refresh(LibrusData cache) {
    }

    @Override
    public void setOnSetupCompleteListener(OnSetupCompleteListener listener) {
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
}
