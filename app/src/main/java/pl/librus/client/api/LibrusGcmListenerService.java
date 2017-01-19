package pl.librus.client.api;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

import pl.librus.client.R;

/**
 * Created by szyme on 15.12.2016. librus-client
 */

public class LibrusGcmListenerService extends GcmListenerService {
    private static final String TAG = "librus-client-log";

    @Override
    public void onMessageReceived(String s, Bundle bundle) {

        //Log values
        String summary = "";
        for (String key : bundle.keySet()) {
            Object o = bundle.get(key);
            String v = o == null ? "" : o.toString();
            summary += key + "  :  " + v + "\n";
        }
        Log.d(TAG, "onMessageReceived: \n" +
                "Sender: " + s + "\n" +
                summary);

        //Send category to analytics
        FirebaseAnalytics fa = FirebaseAnalytics.getInstance(this);
        Bundle event = new Bundle();
        event.putString("objectType", bundle.getString("objectT"));
        fa.logEvent("notification_received", event);
        //Send test notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.btn_plus)
                        .setContentTitle(bundle.getString("message"))
                        .setContentText(bundle.getString("objectType"));
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(4544, mBuilder.build());
        //Start the update
        LibrusDataLoader.load(this).done(new DoneCallback<LibrusData>() {
            @Override
            public void onDone(LibrusData result) {
                LibrusDataLoader.updatePersistent(result, getApplicationContext()).done(new DoneCallback<LibrusData>() {
                    @Override
                    public void onDone(LibrusData result) {
                        LibrusDataLoader.save(result, getApplicationContext());
                    }
                });
            }
        }).fail(new FailCallback<Object>() {
            @Override
            public void onFail(Object result) {
                LibrusDataLoader.updatePersistent(new LibrusData(getApplicationContext()), getApplicationContext()).done(new DoneCallback<LibrusData>() {
                    @Override
                    public void onDone(LibrusData result) {
                        LibrusDataLoader.save(result, getApplicationContext());
                    }
                });
            }
        });
    }
}
