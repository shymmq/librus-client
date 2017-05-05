package pl.librus.client.presentation;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import pl.librus.client.MainActivityScope;
import pl.librus.client.R;
import pl.librus.client.data.EntityChange;
import pl.librus.client.data.LibrusData;
import pl.librus.client.data.UpdateHelper;
import pl.librus.client.domain.Identifiable;
import pl.librus.client.domain.LessonRange;
import pl.librus.client.domain.LibrusUnit;
import pl.librus.client.domain.Teacher;
import pl.librus.client.domain.lesson.EnrichedLesson;
import pl.librus.client.domain.lesson.ImmutableEnrichedLesson;
import pl.librus.client.domain.lesson.ImmutableSchoolWeek;
import pl.librus.client.domain.lesson.Lesson;
import pl.librus.client.domain.lesson.SchoolWeek;
import pl.librus.client.ui.MainActivity;
import pl.librus.client.ui.MainActivityOps;
import pl.librus.client.ui.timetable.TimetableFragment;
import pl.librus.client.ui.timetable.TimetableView;
import pl.librus.client.util.LibrusUtils;

import static java8.util.stream.Collectors.groupingBy;
import static java8.util.stream.Collectors.mapping;
import static java8.util.stream.Collectors.toList;
import static java8.util.stream.StreamSupport.stream;

/**
 * Created by robwys on 28/03/2017.
 */

@MainActivityScope
public class TimetablePresenter extends ReloadablePresenter<List<SchoolWeek>, TimetableView> {

    private final LibrusData data;

    private LocalDate currentWeekStart;

    private final Timer timer = new Timer("lesson-refresh");

    @Inject
    protected TimetablePresenter(MainActivityOps mainActivity, UpdateHelper updateHelper, LibrusData data, ErrorHandler errorHandler) {
        super(mainActivity, updateHelper, errorHandler);
        this.data = data;
    }

    @Override
    public Fragment getFragment() {
        return new TimetableFragment();
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

    @Override
    protected Single<List<SchoolWeek>> fetchData() {
        return data.findUnit()
                .flatMapObservable(this::findInitialSchoolWeeks)
                .toList();
    }

    private Observable<SchoolWeek> findInitialSchoolWeeks(LibrusUnit unit) {
        List<LocalDate> initialWeekStarts = resetWeekStart();
        return Observable.fromIterable(initialWeekStarts)
                .concatMapEager(ws -> findSchoolWeek(ws, unit)
                        .toObservable());
    }

    private Single<SchoolWeek> findSchoolWeek(LocalDate weekStart, LibrusUnit unit) {
        return data.findLessonsForWeek(weekStart)
                .map(enrichLesson(unit))
                .toList()
                .map(lessons -> ImmutableSchoolWeek.of(weekStart, lessons))
                .cast(SchoolWeek.class)
                .doOnSuccess(this::incrementCurrentWeek);
    }

    private Function<Lesson, EnrichedLesson> enrichLesson(LibrusUnit unit) {
        Optional<Integer> currentLessonNo = findCurrentLessonNo(unit.lessonRanges());
        LocalDate today = LocalDate.now();
        return lesson -> ImmutableEnrichedLesson.fromLesson(lesson)
                .withCurrent(lesson.date().equals(today) &&
                        currentLessonNo.isPresent() &&
                        currentLessonNo.get() == lesson.lessonNo());
    }

    @NonNull
    private List<LocalDate> resetWeekStart() {
        currentWeekStart = LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY);
        return Lists.newArrayList(currentWeekStart, currentWeekStart.plusWeeks(1));
    }

    @Override
    protected void displayData(List<SchoolWeek> data) {
        super.displayData(data);
        view.scrollToDay(LocalDate.now());

        startRefreshTimer();
    }

    private Optional<Integer> findCurrentLessonNo(List<LessonRange> ranges) {
        LocalTime now = LocalTime.now();

        for(int i = 0; i < ranges.size(); i++) {
            LessonRange range = ranges.get(i);
            if(!range.to().isPresent()) {
                continue;
            }
            if(range.to().get().compareTo(now) > 0) {
                return Optional.of(i);
            }
        }
        return Optional.absent();
    }

    public void startRefreshTimer() {
        LocalDate today = LocalDate.now();
        subscription = data.findUnit()
                .map(LibrusUnit::lessonRanges)
                .map(ranges -> findCurrentLessonNo(ranges)
                        .transform(ranges::get)
                        .transform(LessonRange::to)

                )
                .filter(Optional::isPresent)
                .map(Optional::get)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(refreshTime -> {
                    if(refreshTime.isPresent()) {
                        LibrusUtils.log("Scheduling refresh task to %s", refreshTime.get());
                        Date date = today.toDateTime(refreshTime.get()).toDate();
                        timer.schedule(new RefreshTask(), date);
                    }
                }, errorHandler);
    }

    private class RefreshTask extends TimerTask {
        @Override
        public void run() {
            refresh();
            LibrusUtils.log("Refreshing lessons");
        }
    }

    public void loadMore() {
        subscription = data.findUnit()
                .flatMap(unit -> findSchoolWeek(currentWeekStart, unit))
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(ifViewAttached(v -> v.setProgress(false)))
                .subscribe(view::displayMore, errorHandler);
    }

    private void incrementCurrentWeek(SchoolWeek schoolWeek) {
        currentWeekStart = currentWeekStart.plusWeeks(1);
    }

    protected Set<Class<? extends Identifiable>> dependentEntities() {
        return Sets.newHashSet(
                Lesson.class,
                Teacher.class);

    }

    @Override
    protected Observable<? extends EntityChange<? extends Identifiable>> reloadRelevantEntities() {
        return updateHelper.reloadLessons();
    }

    public void lessonClicked(EnrichedLesson lesson) {
        subscription = data.makeFullLesson(lesson)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::displayDetails, errorHandler);
    }

    @Override
    protected void onViewDetached() {
        super.onViewDetached();
        timer.purge();
    }
}
