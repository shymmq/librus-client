package pl.librus.client.api;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.requery.Persistable;
import java8.util.function.Consumer;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import pl.librus.client.datamodel.Announcement;
import pl.librus.client.datamodel.Event;
import pl.librus.client.datamodel.Grade;
import pl.librus.client.datamodel.LuckyNumber;
import pl.librus.client.sql.EntityChange;
import pl.librus.client.sql.UpdateHelper;

import static pl.librus.client.sql.EntityChange.Type.ADDED;

/**
 * Created by szyme on 15.12.2016. librus-client
 */

public class LibrusGcmListenerService extends GcmListenerService {
    private Consumer<String> firebaseLogger;
    private NotificationService notificationService;
    private Flowable<?> reloads;

    @Override
    public ComponentName startService(Intent service) {
        firebaseLogger = s -> {
            Bundle event = new Bundle();
            event.putString("objectType", s);
            FirebaseAnalytics.getInstance(this).logEvent("notification_received", event);
        };
        notificationService = new NotificationService(getApplicationContext());
        return super.startService(service);
    }

    @Override
    public void onMessageReceived(String s, Bundle bundle) {

        //Send category to analytics
        firebaseLogger.accept(bundle.getString("objectT"));

        Single<List<Grade>> gradeChanges = UpdateHelper.reload(Grade.class)
                .map(this::filterAdded)
                .cache();
        gradeChanges.subscribe(notificationService::addGrades);

        Single<List<Announcement>> announcementChanges = UpdateHelper.reload(Announcement.class)
                .map(this::filterAdded)
                .cache();
        announcementChanges.subscribe(notificationService::addAnnouncements);

        Single<List<Event>> eventChanges = UpdateHelper.reload(Event.class)
                .map(this::filterAdded)
                .cache();
        eventChanges.subscribe(notificationService::addEvents);

        Single<List<LuckyNumber>> luckyNumberChanges = UpdateHelper.reload(LuckyNumber.class)
                .map(this::filterAdded)
                .cache();
        luckyNumberChanges.subscribe(notificationService::addLuckyNumber);

        reloads = Single.merge(
                gradeChanges,
                announcementChanges,
                eventChanges,
                luckyNumberChanges
        );
    }

    public Flowable<?> getReloads() {
        return reloads;
    }

    public void setFirebaseLogger(Consumer<String> firebaseLogger) {
        this.firebaseLogger = firebaseLogger;
    }

    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    private <T extends Persistable> List<T> filterAdded(List<EntityChange<T>> changes) {
        return StreamSupport.stream(changes)
                .filter(change -> change.type() == ADDED)
                .map(EntityChange::entity)
                .collect(Collectors.toList());
    }
}
