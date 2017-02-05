package pl.librus.client.timetable;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jdeferred.DoneCallback;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import eu.davidea.flexibleadapter.common.TopSnappedSmoothScroller;
import eu.davidea.flexibleadapter.items.IFlexible;
import pl.librus.client.R;
import pl.librus.client.datamodel.Lesson;
import pl.librus.client.datamodel.LessonType;
import pl.librus.client.sql.UpdateHelper;
import pl.librus.client.ui.MainApplication;
import pl.librus.client.ui.MainFragment;

public class TimetableFragment extends Fragment implements MainFragment {
    final ProgressItem progressItem = new ProgressItem();
    public Runnable onSetupCompleted = new Runnable() {
        @Override
        public void run() {

        }
    };
    TimetableAdapter adapter;
    SmoothScrollLinearLayoutManager layoutManager;
    LocalDate startDate;
    int page = 0;
    IFlexible defaultHeader;
    private OnSetupCompleteListener listener;

    public static TimetableFragment newInstance() {
        return new TimetableFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TopSnappedSmoothScroller.MILLISECONDS_PER_INCH = 15f;
        return inflater.inflate(R.layout.fragment_timetable, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_timetable_recycler);

        recyclerView.setVisibility(View.VISIBLE);

        layoutManager = new SmoothScrollLinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        startDate = TimetableUtils.getLastFullWeekStart(LocalDate.now()).plusWeeks(1);
        List<IFlexible> initialElements = new ArrayList<>();
        List<LocalDate> initialWeekStarts = TimetableUtils.getNextFullWeekStarts(LocalDate.now());

        for (LocalDate weekStart : initialWeekStarts) {
            for (LocalDate date = weekStart; date.isBefore(weekStart.plusWeeks(1)); date = date.plusDays(1)) {

                LessonHeaderItem header = new LessonHeaderItem(date);
                List<Lesson> lessons = MainApplication.getData().select(Lesson.class)
                        .where(LessonType.DATE.eq(date))
                        .get()
                        .toList();
                if (lessons == null || lessons.isEmpty()) {
                    initialElements.add(new EmptyLessonItem(header, date));
                } else {
                    for (Lesson l : lessons) {
                        LessonItem lessonItem = new LessonItem(header, l, getContext());
                        initialElements.add(lessonItem);
                    }
                }
                if (date.equals(LocalDate.now())) {
                    defaultHeader = header;
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
                final LocalDate weekStart = startDate.plusWeeks(page);

                new UpdateHelper(getContext()).getLessonsForWeek(weekStart).done(new DoneCallback<List<Lesson>>() {
                    @Override
                    public void onDone(List<Lesson> result) {
                        SchoolWeek schoolWeek = new SchoolWeek(weekStart, result);
                        for (SchoolDay schoolDay : schoolWeek.getSchoolDays()) {
                            LocalDate date = schoolDay.getDate();
                            LessonHeaderItem header = new LessonHeaderItem(date);
                            if (schoolDay.isEmpty()) {
                                newElements.add(new EmptyLessonItem(header, date));
                            } else {
                                for (Lesson l : schoolDay.getLessons()) {
                                    if(l != null) {
                                        LessonItem lessonItem = new LessonItem(header, l, getContext());
                                        newElements.add(lessonItem);
                                    } else {
                                        //TODO: Add missing lesson item
                                    }

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

        //Scroll to default position after a delay to let recyclerview complete layout
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (defaultHeader != null)
                    recyclerView.smoothScrollToPosition(adapter.getGlobalPositionOf(defaultHeader));
            }
        }, 50);
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
