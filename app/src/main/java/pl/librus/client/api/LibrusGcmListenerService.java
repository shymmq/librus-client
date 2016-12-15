package pl.librus.client.api;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by szyme on 15.12.2016. librus-client
 */

public class LibrusGcmListenerService extends GcmListenerService {
    private static final String TAG = "librus-client-log";

    @Override
    public void onMessageReceived(String s, Bundle bundle) {
        String summary = "";
        for (String key : bundle.keySet()) {
            Object o = bundle.get(key);
            String v = o == null ? "" : o.toString();
            summary += key + "  :  " + v + "\n";
        }
        Log.d(TAG, "onMessageReceived: \n" + summary);
    }
}
