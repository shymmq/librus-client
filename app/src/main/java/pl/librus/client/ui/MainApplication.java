package pl.librus.client.ui;

import android.content.Context;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;

import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveSupport;
import io.requery.sql.EntityDataStore;
import io.requery.sql.TableCreationMode;
import pl.librus.client.BuildConfig;
import pl.librus.client.api.Analytics;
import pl.librus.client.datamodel.Models;
import pl.librus.client.sql.SqlHelper;


public class MainApplication extends MultiDexApplication {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.enableDefaults();
        new Analytics().init(this);
        MainApplication.context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
