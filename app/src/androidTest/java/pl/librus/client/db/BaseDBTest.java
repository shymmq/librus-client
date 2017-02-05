package pl.librus.client.db;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;

import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseProvider;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.meta.EntityModel;
import io.requery.sql.EntityDataStore;
import pl.librus.client.datamodel.Models;
import pl.librus.client.sql.SqlHelper;

public abstract class BaseDBTest {

    protected DatabaseSource dataSource;
    protected EntityDataStore<Persistable> data;

    @Before
    public void setup() {
        String dbName = "test.db";
        Context context = InstrumentationRegistry.getTargetContext();
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