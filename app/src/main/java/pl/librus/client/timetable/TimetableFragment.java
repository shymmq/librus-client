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
import com.google.common.collect.Lists;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import eu.davidea.flexibleadapter.common.TopSnappedSmoothScroller;
import eu.davidea.flexibleadapter.items.IFlexible;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import pl.librus.client.R;
import pl.librus.client.api.LibrusData;
import pl.librus.client.datamodel.Lesson;
import pl.librus.client.datamodel.LessonType;
import pl.librus.client.datamodel.Teacher;
import pl.librus.client.sql.UpdateHelper;
import pl.librus.client.ui.MainApplication;
import pl.librus.client.ui.MainFragment;

public class TimetableFragment extends MainFragment {
    private final ProgressItem progressItem = new ProgressItem();
    private TimetableAdapter adapter;
    private SmoothScrollLinearLayoutManager layoutManager;
    private LocalDate weekStart;
    private IFlexible defaultHeader;

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

        weekStart = LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY);
        List<LocalDate> initialWeekStarts = Lists.newArrayList(weekStart, weekStart.plusWeeks(1));

        Observable.fromIterable(initialWeekStarts)
                .flatMap(ws -> LibrusData.findLessonsForWeek(ws).toObservable())
                .flatMapIterable(l -> l)
                .toList()
                .map(this::mapLessons)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::displayInitial);
    }

    private List<IFlexible> mapLessons(List<Lesson> lessons) {
        List<IFlexible> result = new ArrayList<>();

        Map<LocalDate, List<Lesson>> days = StreamSupport.stream(lessons)
                .collect(Collectors.groupingBy(Lesson::date));
        for (LocalDate date = weekStart; date.isBefore(weekStart.plusWeeks(1)); date = date.plusDays(1)) {
            LessonHeaderItem header = new LessonHeaderItem(date);
            if(date.equals(LocalDate.now())) {
                defaultHeader = header;
            }
            List<Lesson> schoolDay = days.get(date);
            if (schoolDay== null || schoolDay.isEmpty()) {
                result.add(new EmptyLessonItem(header, date));
            } else {
                for (Lesson l : schoolDay) {
                    if (l != null) {
                        LessonItem lessonItem = new LessonItem(header, l, getContext());
                        result.add(lessonItem);
                    } else {
                        //TODO: Add missing lesson item
                    }

                }
            }
        }
        return result;
    }

    private void displayInitial(List<IFlexible> elements) {
        weekStart = weekStart.plusWeeks(2);
        final RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.fragment_timetable_recycler);

        recyclerView.setVisibility(View.VISIBLE);

        layoutManager = new SmoothScrollLinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new TimetableAdapter(elements);

        adapter.setDisplayHeadersAtStartUp(true);
        adapter.setEndlessProgressItem(progressItem);

        adapter.onLoadMoreListener = () -> {
            progressItem.setStatus(ProgressItem.LOADING);
            adapter.notifyItemChanged(adapter.getGlobalPositionOf(progressItem));

            LibrusData.findLessonsForWeek(weekStart)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(this::mapLessons)
                    .subscribe(this::moreLoaded);
        };


        adapter.mItemClickListener = this::onItemClick;
        recyclerView.setAdapter(adapter);

        //Scroll to default position after a delay to let recyclerview complete layout
        new Handler().postDelayed(() -> {
            if (defaultHeader != null)
                recyclerView.smoothScrollToPosition(adapter.getGlobalPositionOf(defaultHeader));
        }, 50);
    }

    private void moreLoaded(List<IFlexible> elements) {
        progressItem.setStatus(ProgressItem.IDLE);
        adapter.onLoadMoreComplete(elements);
        weekStart = weekStart.plusWeeks(1);
    }

    @Override
    public int getTitle() {
        return R.string.timetable_view_title;
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_event_note_black_48dp;
    }

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
                Teacher orgTeacher = LibrusData.findByKey(Teacher.class, lesson.orgTeacher()).blockingGet();
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
