package pl.librus.client.api;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

/**
 * Created by szyme on 15.12.2016. librus-client
 */

public class LibrusGcmListenerService extends GcmListenerService {
    private static final String TAG = "librus-client-log";
    LibrusData data;

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

        //Start the update
        LibrusDataLoader.load(this).done(new DoneCallback<LibrusData>() {
            @Override
            public void onDone(LibrusData result) {
                LibrusDataLoader.updatePersistent(result, getApplicationContext());
            }
        }).fail(new FailCallback<Object>() {
            @Override
            public void onFail(Object result) {
                LibrusDataLoader.updatePersistent(new LibrusData(getApplicationContext()), getApplicationContext());
            }
        });
    }
}
