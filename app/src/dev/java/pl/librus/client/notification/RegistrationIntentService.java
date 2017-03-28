package pl.librus.client.notification;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by mimi89999 on 14.02.2017. Empty class to handle calls from LoginActivity.java:65
 */

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "librus-client-logError";

    public RegistrationIntentService() {
        super("RegistrationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Didn't GCM register.");
    }
}
