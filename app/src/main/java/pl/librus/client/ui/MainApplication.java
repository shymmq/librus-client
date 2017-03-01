package pl.librus.client.ui;

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

    private static ReactiveEntityStore<Persistable> dataStore;

    public static ReactiveEntityStore<Persistable> getData() {
        return dataStore;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.enableDefaults();
        new Analytics().init(this);
    }

    public ReactiveEntityStore<Persistable> initData(String login) {
        if (dataStore == null) {
            DatabaseSource source = new DatabaseSource(this, Models.DEFAULT, databaseName(login), 10);
            if (BuildConfig.DEBUG) {
                source.setLoggingEnabled(true);
                source.setTableCreationMode(TableCreationMode.DROP_CREATE);
            }
            ;
            dataStore = ReactiveSupport.toReactiveStore(SqlHelper.getDataStore(source));
        }
        return dataStore;
    }

    public void deleteData(String login) {
        deleteDatabase(databaseName(login));
        closeData();
    }

    public void closeData() {
        if (dataStore != null) {
            dataStore.close();
            dataStore = null;
        }
    }

    private String databaseName(String login) {
        return "user-data-" + login;
    }


}
