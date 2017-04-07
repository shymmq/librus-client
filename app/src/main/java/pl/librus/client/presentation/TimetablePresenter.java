package pl.librus.client.presentation;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.google.common.collect.Lists;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.librus.client.MainActivityScope;
import pl.librus.client.R;
import pl.librus.client.data.LibrusData;
import pl.librus.client.data.UpdateHelper;
import pl.librus.client.domain.Identifiable;
import pl.librus.client.domain.Teacher;
import pl.librus.client.domain.lesson.ImmutableSchoolWeek;
import pl.librus.client.domain.lesson.Lesson;
import pl.librus.client.domain.lesson.SchoolWeek;
import pl.librus.client.ui.MainActivityOps;
import pl.librus.client.ui.timetable.TimetableFragment;
import pl.librus.client.ui.timetable.TimetableView;

/**
 * Created by robwys on 28/03/2017.
 */

@MainActivityScope
public class TimetablePresenter extends ReloadablePresenter<TimetableView> {

    private final LibrusData data;

    private LocalDate weekStart;

    @Inject
    public TimetablePresenter(LibrusData data, MainActivityOps mainActivity, UpdateHelper updateHelper) {
        super(mainActivity, updateHelper);
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
    protected void onViewAttached() {
        refreshView();
    }

    @Override
    protected void refreshView() {
        List<LocalDate> initialWeekStarts = resetWeekStart();

        Observable.fromIterable(initialWeekStarts)
                .concatMapEager(ws -> data
                        .findLessonsForWeek(ws)
                        .toList()
                        .map(lessons -> ImmutableSchoolWeek.of(ws, lessons))
                        .cast(SchoolWeek.class)
                        .toObservable())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toList()
                .subscribe(this::initialLoaded);
    }

    @NonNull
    private List<LocalDate> resetWeekStart() {
        weekStart = LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY);
        return Lists.newArrayList(weekStart, weekStart.plusWeeks(1));
    }

    private void initialLoaded(List<SchoolWeek> schoolWeeks) {
        weekStart = weekStart.plusWeeks(2);
        view.display(schoolWeeks);
        view.scrollToDay(LocalDate.now());
    }

    public void loadMore() {
        data.findLessonsForWeek(weekStart)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(l -> ImmutableSchoolWeek.of(weekStart, l))
                .subscribe(this::moreLoaded);
    }

    private void moreLoaded(SchoolWeek schoolWeek) {
        view.setProgress(false);
        view.displayMore(schoolWeek);
        weekStart = weekStart.plusWeeks(1);
    }

    @Override
    protected List<Class<? extends Identifiable>> dependentEntities() {
        return Lists.newArrayList(
                Lesson.class,
                Teacher.class);

    }

    //FIXME should be inside FullLesson
    public Teacher getTeacher(Lesson lesson) {
        return data.blocking()
                .getById(Teacher.class, lesson.orgTeacher().get());
    }

    @Override
    public void reload() {
        view.setRefreshing(true);
        updateHelper.reloadLessons(resetWeekStart())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> view.setRefreshing(false))
                .isEmpty()
                .subscribe(empty -> {
                    if (!empty) {
                        refreshView();
                    }
                });
    }
}
