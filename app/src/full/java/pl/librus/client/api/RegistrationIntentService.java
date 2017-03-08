package pl.librus.client.api;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import pl.librus.client.LibrusConstants;

/**
 * Created by szyme on 15.12.2016. librus-client
 */

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "librus-client-logError";
    public static final String APP_ID = "431120868545";

    public RegistrationIntentService() {
        super("RegistrationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean register = intent.getBooleanExtra(LibrusConstants.REGISTER, false);
        try {
            if (register) {
                String token = InstanceID.getInstance(this)
                        .getToken(APP_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                new DefaultAPIClient(this).pushDevices(token).subscribe();
                Log.d(TAG, "GCM token: " + token);
            } else {
                InstanceID.getInstance(this)
                        .deleteToken(APP_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
