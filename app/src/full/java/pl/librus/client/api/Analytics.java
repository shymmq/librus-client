package pl.librus.client.api;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by szyme on 26.02.2017.
 */
public class Analytics implements IAnalytics {
    @Override
    public void init(Application app) {
        FirebaseApp.initializeApp(app);
        FirebaseAnalytics.getInstance(app)
                .setAnalyticsCollectionEnabled(!pl.librus.client.BuildConfig.DEBUG);
    }
}
