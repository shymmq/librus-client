package pl.librus.client.api;

import android.app.Notification;
import android.app.NotificationManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.firebase.analytics.FirebaseAnalytics;

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
        event.putString("objectType", bundle.getString("objectType"));
        fa.logEvent("notification_received", event);


        //TODO replace placeholder text with correct event description
        //Create notification
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Powiadomienie")
                .setContentText(bundle.getString("objectType"))
                .setSmallIcon(R.drawable.ic_message_black_48dp)
                .build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), notification);
    }
}
