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

import io.reactivex.Observable;
import io.reactivex.Single;
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
import pl.librus.client.ui.MainActivity;
import pl.librus.client.ui.MainActivityOps;
import pl.librus.client.ui.timetable.TimetableFragment;
import pl.librus.client.ui.timetable.TimetableView;

/**
 * Created by robwys on 28/03/2017.
 */

@MainActivityScope
public class TimetablePresenter extends ReloadablePresenter<List<SchoolWeek>, TimetableView> {

    private final LibrusData data;

    private LocalDate currentWeekStart;

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
        List<LocalDate> initialWeekStarts = resetWeekStart();
        return Observable.fromIterable(initialWeekStarts)
                .concatMapEager(ws -> data
                        .findLessonsForWeek(ws)
                        .toList()
                        .map(lessons -> ImmutableSchoolWeek.of(ws, lessons))
                        .cast(SchoolWeek.class)
                        .doOnSuccess(this::incrementCurrentWeek)
                        .toObservable())
                .toList();
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
    }

    public void loadMore() {
        subscription = data.findLessonsForWeek(currentWeekStart)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(l -> ImmutableSchoolWeek.of(currentWeekStart, l))
                .doFinally(() -> view.setProgress(false))
                .doOnSuccess(this::incrementCurrentWeek)
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
