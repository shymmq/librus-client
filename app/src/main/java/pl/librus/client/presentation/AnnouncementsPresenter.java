package pl.librus.client.presentation;

import android.support.v4.app.Fragment;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.librus.client.MainActivityScope;
import pl.librus.client.R;
import pl.librus.client.data.LibrusData;
import pl.librus.client.ui.MainActivityOps;
import pl.librus.client.ui.announcements.AnnouncementItem;
import pl.librus.client.ui.announcements.AnnouncementsFragment;

/**
 * Created by robwys on 28/03/2017.
 */

@MainActivityScope
public class AnnouncementsPresenter extends MainFragmentPresenter {

    public static final int TITLE = R.string.announcements_view_title;
    private final LibrusData data;
    private final AnnouncementsFragment fragment;

    @Inject
    public AnnouncementsPresenter(MainActivityOps mainActivity, LibrusData data) {
        super(mainActivity);
        this.data = data;
        this.fragment = new AnnouncementsFragment();
        fragment.setPresenter(this);
    }

    public void refresh() {
        data.findFullAnnouncements()
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(l -> mainActivity.setBackArrow(false))
                .subscribe(fragment::displayList);
    }

    @Override
    public Fragment getFragment() {
        return fragment;
    }

    @Override
    public int getTitle() {
        return TITLE;
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_announcement_black_48dp;
    }

    public void displayDetails(AnnouncementItem announcementItem) {
        mainActivity.setBackArrow(true);
        fragment.displayDetails(announcementItem);
    }

    @Override
    public int getOrder() {
        return 3;
    }
}
