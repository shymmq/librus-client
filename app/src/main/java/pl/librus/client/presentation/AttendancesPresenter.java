package pl.librus.client.presentation;

import android.support.v4.app.Fragment;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.librus.client.MainActivityScope;
import pl.librus.client.R;
import pl.librus.client.data.LibrusData;
import pl.librus.client.domain.attendance.FullAttendance;
import pl.librus.client.ui.MainActivityOps;
import pl.librus.client.ui.attendances.AttendanceFragment;
import pl.librus.client.ui.attendances.AttendancesView;

/**
 * Created by robwys on 28/03/2017.
 */
@MainActivityScope
public class AttendancesPresenter extends MainFragmentPresenter<AttendancesView> {

    private final LibrusData data;

    @Inject
    public AttendancesPresenter(MainActivityOps mainActivity, LibrusData data) {
        super(mainActivity);
        this.data = data;
    }

    @Override
    public int getOrder() {
        return 4;
    }

    @Override
    public Fragment getFragment() {
        return new AttendanceFragment();
    }

    @Override
    public int getTitle() {
        return R.string.attendances_view_title;
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_person_outline_black_48dp;
    }

    @Override
    protected void onViewAttached() {
        refreshView();
    }

    private void refreshView() {
        data.findFullAttendances()
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::display);
    }

    public void attendanceClicked(FullAttendance attendance) {
        view.displayPopup(attendance);
    }
}
