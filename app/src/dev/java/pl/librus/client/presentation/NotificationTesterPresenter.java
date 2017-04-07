package pl.librus.client.presentation;

import android.support.v4.app.Fragment;

import java.util.List;

import javax.inject.Inject;

import io.requery.Persistable;
import pl.librus.client.R;
import pl.librus.client.data.UpdateHelper;
import pl.librus.client.data.db.DatabaseManager;
import pl.librus.client.domain.Event;
import pl.librus.client.domain.LuckyNumber;
import pl.librus.client.domain.announcement.Announcement;
import pl.librus.client.domain.grade.Grade;
import pl.librus.client.notification.NotificationService;
import pl.librus.client.ui.MainActivityOps;
import pl.librus.client.ui.NotificationTesterFragment;

/**
 * Created by robwys on 28/03/2017.
 */

public class NotificationTesterPresenter extends MainFragmentPresenter {

    private final NotificationTesterFragment fragment;
    private final DatabaseManager database;
    private final NotificationService notificationService;

    @Inject
    protected NotificationTesterPresenter(MainActivityOps mainActivity,
                                          DatabaseManager database,
                                          NotificationService notificationService) {
        super(mainActivity);
        this.database = database;
        this.notificationService = notificationService;
        fragment = new NotificationTesterFragment();
        fragment.setPresenter(this);
    }

    @Override
    public int getOrder() {
        return 5;
    }

    @Override
    public Fragment getFragment() {
        return fragment;
    }

    @Override
    public int getTitle() {
        return R.string.notification_tester;
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_notifications_black_24dp;
    }

    private <T extends Persistable> List<T> getMany(Class<T> clazz, int count) {
        return database
                .getAll(clazz)
                .take(count)
                .toList()
                .blockingGet();
    }

    public void sendNotificationClicked(Class<? extends Persistable> clazz, int count) {
        if (clazz.isAssignableFrom(LuckyNumber.class)) {
            notificationService.addLuckyNumber(getMany(LuckyNumber.class, count));
        } else if (clazz.isAssignableFrom(Event.class)) {
            notificationService.addEvents(getMany(Event.class, count));
        } else if (clazz.isAssignableFrom(Grade.class)) {
            notificationService.addGrades(getMany(Grade.class, count));
        } else if (clazz.isAssignableFrom(Announcement.class)) {
            notificationService.addAnnouncements(getMany(Announcement.class, count));
        }
    }

}
