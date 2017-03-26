package pl.librus.client.api;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import pl.librus.client.datamodel.Event;
import pl.librus.client.datamodel.LuckyNumber;
import pl.librus.client.datamodel.announcement.Announcement;
import pl.librus.client.datamodel.grade.Grade;
import pl.librus.client.sql.EntityChange;
import pl.librus.client.sql.UpdateHelper;

import static pl.librus.client.sql.EntityChange.Type.ADDED;

/**
 * Created by szyme on 15.12.2016. librus-client
 */

public class LibrusGcmListenerService extends GcmListenerService {
    private NotificationService notificationService;
    private UpdateHelper updateHelper;

    public LibrusGcmListenerService() {
    }

    public LibrusGcmListenerService(NotificationService notificationService, UpdateHelper updateHelper) {
        this.notificationService = notificationService;
        this.updateHelper = updateHelper;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        updateHelper = new UpdateHelper(this);
        notificationService = new NotificationService(this);
    }

    @Override
    public void onMessageReceived(String s, Bundle bundle) {

        updateHelper.reload(Grade.class)
                .compose(this::filterAdded)
                .toList()
                .subscribe(notificationService::addGrades);

        updateHelper.reload(Announcement.class)
                .compose(this::filterAdded)
                .toList()
                .subscribe(notificationService::addAnnouncements);

        updateHelper.reload(Event.class)
                .compose(this::filterAdded)
                .toList()
                .subscribe(notificationService::addEvents);

        updateHelper.reload(LuckyNumber.class)
                .compose(this::filterAdded)
                .toList()
                .subscribe(notificationService::addLuckyNumber);
    }

    private <T extends Persistable> Observable<T> filterAdded(Observable<EntityChange<T>> upstream) {
        return upstream
                .filter(change -> change.type() == ADDED)
                .map(EntityChange::entity)
                .observeOn(Schedulers.trampoline());
    }
}
