package pl.librus.client.api;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

import java.util.List;

import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
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
    private LibrusData librusData;

    public LibrusGcmListenerService() {
    }

    public LibrusGcmListenerService(NotificationService notificationService, LibrusData librusData) {
        this.notificationService = notificationService;
        this.librusData = librusData;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        librusData = LibrusData.getInstance(this);
        notificationService = new NotificationService(this, librusData);
    }

    @Override
    public void onMessageReceived(String s, Bundle bundle) {
        UpdateHelper updateHelper = new UpdateHelper(librusData);

        updateHelper.reload(Grade.class)
                .map(this::filterAdded)
                .observeOn(Schedulers.trampoline())
                .subscribe(notificationService::addGrades);

        updateHelper.reload(Announcement.class)
                .map(this::filterAdded)
                .observeOn(Schedulers.trampoline())
                .subscribe(notificationService::addAnnouncements);

        updateHelper.reload(Event.class)
                .map(this::filterAdded)
                .observeOn(Schedulers.trampoline())
                .subscribe(notificationService::addEvents);

        updateHelper.reload(LuckyNumber.class)
                .map(this::filterAdded)
                .observeOn(Schedulers.trampoline())
                .subscribe(notificationService::addLuckyNumber);
    }

    private <T extends Persistable> List<T> filterAdded(List<EntityChange<T>> changes) {
        return StreamSupport.stream(changes)
                .filter(change -> change.type() == ADDED)
                .map(EntityChange::entity)
                .collect(Collectors.toList());
    }
}
