package pl.librus.client.presentation;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.librus.client.MainActivityScope;
import pl.librus.client.R;
import pl.librus.client.data.EntityChange;
import pl.librus.client.data.LibrusData;
import pl.librus.client.data.UpdateHelper;
import pl.librus.client.domain.Identifiable;
import pl.librus.client.domain.Teacher;
import pl.librus.client.domain.lesson.ImmutableSchoolWeek;
import pl.librus.client.domain.lesson.Lesson;
import pl.librus.client.domain.lesson.SchoolWeek;
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
    protected TimetablePresenter(UpdateHelper updateHelper, LibrusData data, ErrorHandler errorHandler) {
        super(updateHelper, errorHandler);
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
    protected Completable refreshView() {
        List<LocalDate> initialWeekStarts = resetWeekStart();

        return Observable.fromIterable(initialWeekStarts)
                .concatMapEager(ws -> data
                        .findLessonsForWeek(ws)
                        .toList()
                        .map(lessons -> ImmutableSchoolWeek.of(ws, lessons))
                        .cast(SchoolWeek.class)
                        .toObservable())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toList()
                .flatMapCompletable(weeks -> Completable.fromAction(() -> initialLoaded(weeks)));
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
        subscription = data.findLessonsForWeek(weekStart)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(l -> ImmutableSchoolWeek.of(weekStart, l))
                .subscribe(this::moreLoaded, errorHandler);
    }

    private void moreLoaded(SchoolWeek schoolWeek) {
        view.setProgress(false);
        view.displayMore(schoolWeek);
        weekStart = weekStart.plusWeeks(1);
    }

    protected Set<Class<? extends Identifiable>> dependentEntities() {
        return Sets.newHashSet(
                Lesson.class,
                Teacher.class);

    }

    //FIXME should be inside FullLesson
    public Teacher getTeacher(Lesson lesson) {
        return data.blocking()
                .getById(Teacher.class, lesson.orgTeacher().get());
    }

    @Override
    protected Observable<? extends EntityChange<? extends Identifiable>> reloadRelevantEntities() {
        return updateHelper.reloadLessons();
    }
}
