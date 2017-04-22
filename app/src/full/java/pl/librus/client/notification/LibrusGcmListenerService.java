package pl.librus.client.notification;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.requery.Persistable;
import pl.librus.client.MainApplication;
import pl.librus.client.data.EntityChange;
import pl.librus.client.domain.event.Event;
import pl.librus.client.domain.LuckyNumber;
import pl.librus.client.domain.announcement.Announcement;
import pl.librus.client.domain.grade.Grade;
import pl.librus.client.data.UpdateHelper;

/**
 * Created by szyme on 15.12.2016. librus-client
 */

public class LibrusGcmListenerService extends GcmListenerService {
    @Inject
    NotificationService notificationService;

    @Inject
    UpdateHelper updateHelper;

    public LibrusGcmListenerService() {
    }

    public LibrusGcmListenerService(NotificationService notificationService, UpdateHelper updateHelper) {
        this.notificationService = notificationService;
        this.updateHelper = updateHelper;
    }

    @Override
    public void onCreate() {
        MainApplication.getOrCreateUserComponent(this)
                .inject(this);
    }

    @Override
    public void onMessageReceived(String s, Bundle bundle) {

        List<Grade> grades = updateHelper.reload(Grade.class)
                .compose(this::filterAdded)
                .toList()
                .blockingGet();
        notificationService.addGrades(grades);

        List<Announcement> announcements = updateHelper.reload(Announcement.class)
                .compose(this::filterAdded)
                .toList()
                .blockingGet();
        notificationService.addAnnouncements(announcements);

        List<Event> events = updateHelper.reload(Event.class)
                .compose(this::filterAdded)
                .toList()
                .blockingGet();
        notificationService.addEvents(events);

        List<LuckyNumber> luckyNumbers = updateHelper.reload(LuckyNumber.class)
                .compose(this::filterAdded)
                .toList()
                .blockingGet();
        notificationService.addLuckyNumber(luckyNumbers);
    }

    private <T extends Persistable> Observable<T> filterAdded(Observable<EntityChange<T>> upstream) {
        return upstream
                .filter(change -> change.type() == EntityChange.Type.ADDED)
                .map(EntityChange::entity);
    }
}
