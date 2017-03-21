package pl.librus.client.api;

import android.app.Application;

import com.google.firebase.analytics.FirebaseAnalytics;

import pl.librus.client.BuildConfig;

/**
 * Created by szyme on 26.02.2017.
 */
public class Analytics implements IAnalytics {
    @Override
    public void init(Application app) {
        FirebaseAnalytics.getInstance(app)
                .setAnalyticsCollectionEnabled(!BuildConfig.DEBUG);
    }
}
