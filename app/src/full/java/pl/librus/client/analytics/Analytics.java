package pl.librus.client.analytics;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by szyme on 26.02.2017.
 */
@Singleton
public class Analytics implements IAnalytics {

    private final Context context;

    @Inject
    public Analytics(Context context) {
        this.context = context;
    }

    public void init() {
        FirebaseApp.initializeApp(context);
        FirebaseAnalytics.getInstance(context)
                .setAnalyticsCollectionEnabled(!pl.librus.client.BuildConfig.DEBUG);
    }
}
