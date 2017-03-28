package pl.librus.client.ui.timetable;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;
import java.util.Locale;

import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import eu.davidea.flexibleadapter.common.TopSnappedSmoothScroller;
import eu.davidea.flexibleadapter.items.IFlexible;
import pl.librus.client.R;
import pl.librus.client.domain.Teacher;
import pl.librus.client.domain.lesson.Lesson;
import pl.librus.client.presentation.TimetablePresenter;

public class TimetableFragment extends Fragment {
    private final ProgressItem progressItem = new ProgressItem();
    private TimetableAdapter adapter;
    private SmoothScrollLinearLayoutManager layoutManager;
    private IFlexible defaultHeader;
    private SwipeRefreshLayout refreshLayout;

    private TimetablePresenter presenter;

    public TimetableFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TopSnappedSmoothScroller.MILLISECONDS_PER_INCH = 15f;
        return inflater.inflate(R.layout.fragment_timetable, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.refresh();
    }


    public void displayInitial(List<IFlexible> elements) {
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.fragment_timetable_recycler);
        refreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.fragment_timetable_refresh_layout);

        layoutManager = new SmoothScrollLinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        refreshLayout.setColorSchemeResources(R.color.md_blue_grey_400, R.color.md_blue_grey_500, R.color.md_blue_grey_600);
        refreshLayout.setOnRefreshListener(presenter::refresh);
        refreshLayout.setRefreshing(false);

        adapter = new TimetableAdapter(elements);

        adapter.setDisplayHeadersAtStartUp(true);
        adapter.setEndlessProgressItem(progressItem);

        adapter.onLoadMoreListener = () -> {
            progressItem.setStatus(ProgressItem.LOADING);
            adapter.notifyItemChanged(adapter.getGlobalPositionOf(progressItem));
            presenter.loadMore();
        };


        adapter.mItemClickListener = this::onItemClick;
        recyclerView.setAdapter(adapter);

        //Scroll to default position after a delay to let recyclerview complete layout
        new Handler().postDelayed(() -> {
            if (defaultHeader != null)
                recyclerView.smoothScrollToPosition(adapter.getGlobalPositionOf(defaultHeader));
        }, 50);
    }


    public void setProgress(boolean enabled) {
        progressItem.setStatus(enabled ? ProgressItem.LOADING : ProgressItem.IDLE);
    }

    public void updateElements(List<IFlexible> elements) {
        adapter.onLoadMoreComplete(elements);
    }

    public boolean onItemClick(int position) {
        IFlexible item = adapter.getItem(position);
        if (item instanceof LessonItem) {
            Lesson lesson = ((LessonItem) item).getLesson();
            MaterialDialog.Builder builder = new MaterialDialog.Builder(getContext()).title(lesson.subject().name()).positiveText("Zamknij");

            LayoutInflater inflater = LayoutInflater.from(getContext());
            View details = inflater.inflate(R.layout.lesson_details, null);

            ViewGroup eventContainer = (ViewGroup) details.findViewById(R.id.lesson_details_event_container);
            ViewGroup teacherContainer = (ViewGroup) details.findViewById(R.id.lesson_details_teacher_container);

            TextView teacherTextView = (TextView) details.findViewById(R.id.lesson_details_teacher_value);
            TextView dateTextView = (TextView) details.findViewById(R.id.lesson_details_date_value);
            TextView timeTextView = (TextView) details.findViewById(R.id.lesson_details_time_value);
            TextView eventTextView = (TextView) details.findViewById(R.id.lesson_details_event_value);

            dateTextView.setText(new SpannableStringBuilder()
                    .append(lesson.date().toString("EEEE, d MMMM yyyy", new Locale("pl")),
                            new StyleSpan(Typeface.BOLD), Spanned.SPAN_INCLUSIVE_INCLUSIVE));
            SpannableStringBuilder timeSSB = new SpannableStringBuilder();
            if (lesson.hourFrom().isPresent() && lesson.hourTo().isPresent()) {
                timeSSB.append(lesson.hourFrom().get().toString("HH:mm"))
                        .append(" - ")
                        .append(lesson.hourTo().get().toString("HH:mm"))
                        .append(' ');
            }
            timeSSB.append(String.valueOf(lesson.lessonNo()),
                    new StyleSpan(Typeface.BOLD), Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                    .append(". lekcja", new StyleSpan(Typeface.BOLD), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            timeTextView.setText(timeSSB);

            //TODO add Events
            if (lesson.teacher().name().isPresent()) {
                teacherContainer.setVisibility(View.VISIBLE);
                SpannableStringBuilder ssb = new SpannableStringBuilder();
                if (lesson.substitutionClass() && lesson.orgTeacher().isPresent()) {
                    Teacher orgTeacher = presenter.getTeacher(lesson);
                    if (orgTeacher.name().isPresent()) {
                        ssb
                                .append(orgTeacher.name().get())
                                .append(" -> ");
                    }
                }
                ssb.append(lesson.teacher().name().get(),
                        new StyleSpan(Typeface.BOLD), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                teacherTextView.setText(ssb);
            } else {
                teacherContainer.setVisibility(View.GONE);
            }

            eventContainer.setVisibility(View.GONE);

            //TODO Ogarnianie odwołań
            builder.customView(details, true).show();
            return true;
        } else {
            return false;
        }
    }

    public void setDefaultHeader(IFlexible defaultHeader) {
        this.defaultHeader = defaultHeader;
    }

    public void setPresenter(TimetablePresenter presenter) {
        this.presenter = presenter;
    }
}
