package pl.librus.client.api;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

import java.io.IOException;

/**
 * Created by szyme on 15.12.2016. librus-client
 */

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "librus-client-logError";

    public RegistrationIntentService() {
        super("RegistrationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken("431120868545", GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            new APIClient(this).pushDevices(token).done(new DoneCallback<Integer>() {
                @Override
                public void onDone(Integer result) {
                    Log.d(TAG, "Device registered");
                }
            }).fail(new FailCallback<Integer>() {
                @Override
                public void onFail(Integer result) {
                    Log.d(TAG, "Registration failed: code " + result);
                }
            });
            Log.d(TAG, "GCM token: " + token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
