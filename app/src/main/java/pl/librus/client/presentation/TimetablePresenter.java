package pl.librus.client.presentation;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import eu.davidea.flexibleadapter.items.IFlexible;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import pl.librus.client.MainActivityScope;
import pl.librus.client.R;
import pl.librus.client.data.LibrusData;
import pl.librus.client.domain.Teacher;
import pl.librus.client.domain.lesson.Lesson;
import pl.librus.client.ui.MainActivityOps;
import pl.librus.client.ui.timetable.EmptyDayItem;
import pl.librus.client.ui.timetable.LessonHeaderItem;
import pl.librus.client.ui.timetable.LessonItem;
import pl.librus.client.ui.timetable.MissingLessonItem;
import pl.librus.client.ui.timetable.TimetableFragment;

/**
 * Created by robwys on 28/03/2017.
 */

@MainActivityScope
public class TimetablePresenter extends MainFragmentPresenter {

    private final LibrusData data;

    private final Context context;
    private TimetableFragment fragment;
    private LocalDate weekStart;

    @Inject
    public TimetablePresenter(LibrusData data, Context context, MainActivityOps mainActivity) {
        super(mainActivity);
        this.data = data;
        this.context = context;
        this.fragment = new TimetableFragment();
        fragment.setPresenter(this);
    }

    @Override
    public Fragment getFragment() {
        return fragment;
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
    public int getOrder() {
        return 1;
    }

    public void refresh() {
        weekStart = LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY);
        List<LocalDate> initialWeekStarts = Lists.newArrayList(weekStart, weekStart.plusWeeks(1));

        Observable.fromIterable(initialWeekStarts)
                .flatMapSingle(ws -> data
                        .findLessonsForWeek(ws)
                        .toList()
                        .map(mapLessonsForWeek(ws)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .concatMapIterable(i -> i)
                .toList()
                .doOnSuccess(list -> weekStart = weekStart.plusWeeks(2))
                .subscribe(fragment::displayInitial);
    }

    public void loadMore() {
        data.findLessonsForWeek(weekStart)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(mapLessonsForWeek(weekStart))
                .subscribe(this::moreLoaded);
    }

    private void moreLoaded(List<IFlexible> elements) {
        fragment.setProgress(false);
        fragment.updateElements(elements);
        weekStart = weekStart.plusWeeks(1);
    }

    private Function<List<Lesson>, List<IFlexible>> mapLessonsForWeek(LocalDate weekStart) {
        return lessons -> {
            List<IFlexible> result = new ArrayList<>();

            Map<LocalDate, List<Lesson>> days = StreamSupport.stream(lessons)
                    .collect(Collectors.groupingBy(Lesson::date));
            for (LocalDate date = weekStart; date.isBefore(weekStart.plusWeeks(1)); date = date.plusDays(1)) {
                LessonHeaderItem header = new LessonHeaderItem(date);
                if (date.equals(LocalDate.now())) {
                    fragment.setDefaultHeader(header);
                }
                List<Lesson> schoolDay = days.get(date);
                if (schoolDay == null || schoolDay.isEmpty()) {
                    result.add(new EmptyDayItem(header, date));
                } else {
                    ImmutableMap<Integer, Lesson> lessonMap = Maps.uniqueIndex(schoolDay, Lesson::lessonNo);

                    int maxLessonNumber = Collections.max(lessonMap.keySet());
                    int minLessonNumber = Collections.min(lessonMap.keySet());

                    minLessonNumber = Math.min(1, minLessonNumber);

                    for (int l = minLessonNumber; l <= maxLessonNumber; l++) {
                        Lesson lesson = lessonMap.get(l);
                        if (lesson != null) {
                            result.add(new LessonItem(header, lesson, context));
                        } else {
                            result.add(new MissingLessonItem(header, l));
                        }
                    }
                }
            }
            return result;
        };
    }

    //FIXME should be inside FullLesson
    public Teacher getTeacher(Lesson lesson) {
        return data.blocking()
                .getById(Teacher.class, lesson.orgTeacher().get());
    }
}
