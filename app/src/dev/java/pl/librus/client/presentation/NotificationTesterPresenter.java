package pl.librus.client.presentation;

import android.content.Context;
import android.support.v4.app.Fragment;

import java.util.List;

import javax.inject.Inject;

import io.requery.Persistable;
import pl.librus.client.R;
import pl.librus.client.data.db.DatabaseManager;
import pl.librus.client.domain.LuckyNumber;
import pl.librus.client.domain.announcement.Announcement;
import pl.librus.client.domain.event.Event;
import pl.librus.client.domain.grade.Grade;
import pl.librus.client.notification.NotificationService;
import pl.librus.client.ui.NotificationTesterFragment;
import pl.librus.client.ui.NotificationTesterView;
import pl.librus.client.widget.LuckyNumberWidgetProvider;
import pl.librus.client.widget.WidgetUpdater;

/**
 * Created by robwys on 28/03/2017.
 */

public class NotificationTesterPresenter extends MainFragmentPresenter<NotificationTesterView> {

    private final DatabaseManager database;
    private final NotificationService notificationService;
    private final WidgetUpdater widgetUpdater;

    @Inject
    protected NotificationTesterPresenter(
            DatabaseManager database,
            NotificationService notificationService,
            WidgetUpdater widgetUpdater) {
        this.widgetUpdater = widgetUpdater;
        this.database = database;
        this.notificationService = notificationService;
    }

    @Override
    public int getOrder() {
        return 5;
    }

    @Override
    public Fragment getFragment() {
        return new NotificationTesterFragment();
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
            widgetUpdater.updateLuckyNumber();
        } else if (clazz.isAssignableFrom(Event.class)) {
            notificationService.addEvents(getMany(Event.class, count));
        } else if (clazz.isAssignableFrom(Grade.class)) {
            notificationService.addGrades(getMany(Grade.class, count));
        } else if (clazz.isAssignableFrom(Announcement.class)) {
            notificationService.addAnnouncements(getMany(Announcement.class, count));
        }
    }

}
