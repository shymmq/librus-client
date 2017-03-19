package pl.librus.client.ui;

import android.content.Context;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;

import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import pl.librus.client.LibrusUtils;
import pl.librus.client.api.Analytics;
import pl.librus.client.api.HttpException;
import pl.librus.client.api.OfflineException;


public class MainApplication extends MultiDexApplication {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.enableDefaults();
        new Analytics().init(this);
        Consumer<Throwable> originalErrorHandler = RxJavaPlugins.getErrorHandler();
        RxJavaPlugins.setErrorHandler(throwable -> {
            if (throwable instanceof HttpException) {
                //If there are many requests sent at once, first error is handler normally, the rest lands here
                LibrusUtils.log("plugin handle");
                LibrusUtils.log(throwable);
            } else {
                originalErrorHandler.accept(throwable);
            }
        });
        MainApplication.context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
