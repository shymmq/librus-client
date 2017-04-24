package pl.librus.client.analytics;

import android.app.Application;
import android.content.Context;

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
        //TODO: initialize ACRA here
    }
}
