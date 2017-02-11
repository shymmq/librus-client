package pl.librus.client.db;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.robolectric.RuntimeEnvironment;

import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.sql.EntityDataStore;
import pl.librus.client.datamodel.Models;
import pl.librus.client.sql.SqlHelper;

public abstract class BaseDBTest {

    protected DatabaseSource dataSource;
    protected EntityDataStore<Persistable> data;

    @Before
    public void setup() {
        String dbName = "test.db";

        Context context = RuntimeEnvironment.application;
        context.deleteDatabase(dbName);
        dataSource = new DatabaseSource(context, Models.DEFAULT, dbName, 10);
        dataSource.setLoggingEnabled(true);
        data = SqlHelper.getDataStore(dataSource);
    }

    protected void clearCache() {
        data = SqlHelper.getDataStore(dataSource);
    }

    @After
    public void teardown() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}