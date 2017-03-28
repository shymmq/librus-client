package pl.librus.client.presentation;

import android.support.v4.app.Fragment;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.librus.client.MainActivityScope;
import pl.librus.client.R;
import pl.librus.client.data.LibrusData;
import pl.librus.client.ui.MainActivityOps;
import pl.librus.client.ui.attendances.AttendanceFragment;

/**
 * Created by robwys on 28/03/2017.
 */
@MainActivityScope
public class AttendancesPresenter extends MainFragmentPresenter {

    private final LibrusData data;
    private final AttendanceFragment fragment;

    @Inject
    public AttendancesPresenter(MainActivityOps mainActivity, LibrusData data) {
        super(mainActivity);
        this.data = data;
        this.fragment = new AttendanceFragment();
        fragment.setPresenter(this);
    }

    @Override
    public int getOrder() {
        return 4;
    }

    @Override
    public Fragment getFragment() {
        return fragment;
    }

    @Override
    public int getTitle() {
        return R.string.attendances_view_title;
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_person_outline_black_48dp;
    }

    public void refresh() {
        data.findFullAttendances()
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(fragment::displayList);
    }

}
