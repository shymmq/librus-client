package pl.librus.client.ui;

import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveSupport;
import io.requery.sql.Configuration;
import io.requery.sql.ConfigurationBuilder;
import io.requery.sql.EntityDataStore;
import io.requery.sql.GenericMapping;
import io.requery.sql.Platform;
import io.requery.sql.TableCreationMode;
import io.requery.sql.platform.SQLite;
import pl.librus.client.BuildConfig;
import pl.librus.client.LibrusUtils;
import pl.librus.client.datamodel.Models;
import pl.librus.client.sql.LocalDateConverter;
import pl.librus.client.sql.LocalTimeConverter;
import pl.librus.client.sql.SqlHelper;

/**
 * Created by robwys on 04/02/2017.
 */

public class MainApplication extends MultiDexApplication {

    private static EntityDataStore<Persistable> dataStore;

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.enableDefaults();
    }

    public EntityDataStore<Persistable> initData() {
        if(dataStore == null) {
            DatabaseSource source = new DatabaseSource(this, Models.DEFAULT, 5);
            if (BuildConfig.DEBUG) {
                source.setLoggingEnabled(true);
                source.setTableCreationMode(TableCreationMode.DROP_CREATE);
            }
            dataStore = SqlHelper.getDataStore(source);
        }
        return dataStore;
    }

    public void closeData() {
        if(dataStore != null) {
            dataStore.close();
            dataStore = null;
        }
    }

    public static EntityDataStore<Persistable> getData() {
        return dataStore;
    }



}
