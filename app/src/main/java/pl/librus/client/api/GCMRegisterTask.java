package pl.librus.client.api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.jdeferred.DoneCallback;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by szyme on 14.12.2016. librus-client
 */
public class GCMRegisterTask extends AsyncTask<Void, Void, String> {
    private final String TAG = "librus-client-log";
    String SENDER_ID = "522290628608";
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    Context context;

    public GCMRegisterTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        String regToken = null;
        try {
            regToken = GoogleCloudMessaging.getInstance(context).register(SENDER_ID);
            new APIClient(context)
                    .pushDevices(regToken)
                    .done(new DoneCallback<Integer>() {
                        @Override
                        public void onDone(Integer result) {
                            Log.d(TAG, "Device registered");
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, regToken);


//            SyncHttpClient client = new SyncHttpClient();
//            String access_token = preferences.getString("access_token", null);
//
//            client.addHeader("Authorization", "Bearer " + access_token);
//
//            jsonParams.put("device", regToken);
//            jsonParams.put("provider", "Android");

        //send request here

        return regToken;
    }
}