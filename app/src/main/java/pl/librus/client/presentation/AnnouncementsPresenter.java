package pl.librus.client.presentation;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import io.reactivex.Single;
import pl.librus.client.MainActivityScope;
import pl.librus.client.R;
import pl.librus.client.data.LibrusData;
import pl.librus.client.data.UpdateHelper;
import pl.librus.client.domain.Identifiable;
import pl.librus.client.domain.Teacher;
import pl.librus.client.domain.announcement.Announcement;
import pl.librus.client.domain.announcement.FullAnnouncement;
import pl.librus.client.ui.MainActivityOps;
import pl.librus.client.ui.MenuAction;
import pl.librus.client.ui.ReadAllMenuAction;
import pl.librus.client.ui.announcements.AnnouncementItem;
import pl.librus.client.ui.announcements.AnnouncementsFragment;
import pl.librus.client.ui.announcements.AnnouncementsView;

/**
 * Created by robwys on 28/03/2017.
 */

@MainActivityScope
public class AnnouncementsPresenter extends ReloadablePresenter<List<FullAnnouncement>, AnnouncementsView> {

    public static final int TITLE = R.string.announcements_view_title;
    private final LibrusData data;
    private final Context context;

    @Inject
    protected AnnouncementsPresenter(MainActivityOps mainActivity,
                                     UpdateHelper updateHelper,
                                     LibrusData data,
                                     ErrorHandler errorHandler,
                                     Context context) {
        super(mainActivity, updateHelper, errorHandler);
        this.data = data;
        this.context = context;
    }

    @Override
    protected Single<List<FullAnnouncement>> fetchData() {
        return data.findFullAnnouncements()
                .toList();
    }

    @Override
    protected List<MenuAction> getMenuActions(List<FullAnnouncement> data) {
        ReadAllMenuAction reloadAll = new ReadAllMenuAction(data, context, this);

        return ImmutableList.<MenuAction>builder()
                .add(reloadAll)
                .addAll(super.getMenuActions(data))
                .build();
    }

    @Override
    public Fragment getFragment() {
        return new AnnouncementsFragment();
    }

    @Override
    public int getTitle() {
        return TITLE;
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_announcement_black_48dp;
    }

    @Override
    protected void displayData(List<FullAnnouncement> data) {
        mainActivity.setBackArrow(false);
        super.displayData(data);
    }

    public void displayDetails(AnnouncementItem announcementItem) {
        mainActivity.setBackArrow(true);
        view.displayDetails(announcementItem);
    }

    @Override
    public int getOrder() {
        return 3;
    }

    @Override
    protected Set<Class<? extends Identifiable>> dependentEntities() {
        return Sets.newHashSet(
                Announcement.class,
                Teacher.class);
    }
}
