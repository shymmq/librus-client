package pl.librus.client.timetable;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import eu.davidea.flexibleadapter.common.TopSnappedSmoothScroller;
import eu.davidea.flexibleadapter.items.IFlexible;
import io.reactivex.android.schedulers.AndroidSchedulers;
import pl.librus.client.R;
import pl.librus.client.datamodel.Lesson;
import pl.librus.client.datamodel.LessonType;
import pl.librus.client.datamodel.Teacher;
import pl.librus.client.sql.UpdateHelper;
import pl.librus.client.ui.MainApplication;
import pl.librus.client.ui.MainFragment;

public class TimetableFragment extends MainFragment implements FlexibleAdapter.OnItemClickListener {
    private final ProgressItem progressItem = new ProgressItem();
    private TimetableAdapter adapter;
    private SmoothScrollLinearLayoutManager layoutManager;
    private LocalDate startDate;
    private int page = 0;
    private IFlexible defaultHeader;

    public TimetableFragment() {
    }

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

        adapter.onLoadMoreListener = () -> {
            progressItem.setStatus(ProgressItem.LOADING);
            adapter.notifyItemChanged(adapter.getGlobalPositionOf(progressItem));
            LocalDate weekStart = startDate.plusWeeks(page);

            new UpdateHelper(getContext()).getLessonsForWeek(weekStart)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::displayLessons);
        };
        adapter.mItemClickListener = this;
        recyclerView.setAdapter(adapter);

        //Scroll to default position after a delay to let recyclerview complete layout
        new Handler().postDelayed(() -> {
            if (defaultHeader != null)
                recyclerView.smoothScrollToPosition(adapter.getGlobalPositionOf(defaultHeader));
        }, 50);
    }

    private void displayLessons(List<Lesson> lessons) {
        LocalDate weekStart = startDate.plusWeeks(page);
        List<IFlexible> newElements = new ArrayList<>();
        SchoolWeek schoolWeek = new SchoolWeek(weekStart, lessons);
        for (SchoolDay schoolDay : schoolWeek.getSchoolDays()) {
            LocalDate date = schoolDay.getDate();
            LessonHeaderItem header = new LessonHeaderItem(date);
            if (schoolDay.isEmpty()) {
                newElements.add(new EmptyLessonItem(header, date));
            } else {
                for (Lesson l : schoolDay.getLessons()) {
                    if (l != null) {
                        LessonItem lessonItem = new LessonItem(header, l, getContext());
                        newElements.add(lessonItem);
                    } else {
                        //TODO: Add missing lesson item
                    }

                }
            }
        }
        getActivity().runOnUiThread(() -> {
            progressItem.setStatus(ProgressItem.IDLE);
            adapter.onLoadMoreComplete(newElements);
            page++;
        });
    }

    @Override
    public int getTitle() {
        return R.string.timetable_view_title;
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_event_note_black_48dp;
    }

    @Override
    public boolean onItemClick(int position) {
        IFlexible item = adapter.getItem(position);
        if (item instanceof LessonItem) {
            Lesson lesson = ((LessonItem) item).getLesson();
            MaterialDialog.Builder builder = new MaterialDialog.Builder(getContext()).title(lesson.subject().name()).positiveText("Zamknij");

            LayoutInflater inflater = LayoutInflater.from(getContext());
            View details = inflater.inflate(R.layout.lesson_details, null);

            ViewGroup eventContainer = (ViewGroup) details.findViewById(R.id.lesson_details_event_container);

            TextView teacherTextView = (TextView) details.findViewById(R.id.lesson_details_teacher_value);
            TextView dateTextView = (TextView) details.findViewById(R.id.lesson_details_date_value);
            TextView timeTextView = (TextView) details.findViewById(R.id.lesson_details_time_value);
            TextView eventTextView = (TextView) details.findViewById(R.id.lesson_details_event_value);

            dateTextView.setText(new SpannableStringBuilder()
                    .append(lesson.date().toString("EEEE, d MMMM yyyy", new Locale("pl")),
                            new StyleSpan(Typeface.BOLD), Spanned.SPAN_INCLUSIVE_INCLUSIVE));
            timeTextView.setText(new SpannableStringBuilder()
                    .append(lesson.hourFrom().toString("HH:mm"))
                    .append(" - ")
                    .append(lesson.hourTo().toString("HH:mm"))
                    .append(' ')
                    .append(String.valueOf(lesson.lessonNo()),
                            new StyleSpan(Typeface.BOLD), Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                    .append(". lekcja", new StyleSpan(Typeface.BOLD), Spanned.SPAN_INCLUSIVE_INCLUSIVE));

            //TODO add Events

            if (!lesson.substitutionClass() || lesson.orgTeacher() == null) {
                teacherTextView.setText(new SpannableStringBuilder()
                        .append(lesson.teacher().name(),
                                new StyleSpan(Typeface.BOLD), Spanned.SPAN_INCLUSIVE_INCLUSIVE));
            } else {
                Teacher orgTeacher = MainApplication.getData().findByKey(Teacher.class, lesson.orgTeacher()).blockingGet();
                teacherTextView.setText(new SpannableStringBuilder()
                        .append(orgTeacher.name())
                        .append(" -> ")
                        .append(lesson.teacher().name(),
                                new StyleSpan(Typeface.BOLD), Spanned.SPAN_INCLUSIVE_INCLUSIVE));
            }
            eventContainer.setVisibility(View.GONE);

            //TODO Ogarnianie odwołań
            builder.customView(details, true).show();
            return true;
        } else {
            return false;
        }
    }
}
