package pl.librus.client.db;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import io.requery.Persistable;
import io.requery.sql.EntityDataStore;
import pl.librus.client.ui.MainApplication;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;

@Config(application = MainApplication.class)
public abstract class BaseDBTest {

    public static final String DB_NAME = "test";
    MainApplication app;
    protected EntityDataStore<Persistable> data;

    @Before
    public void setup() {
        app = (pl.librus.client.ui.MainApplication) RuntimeEnvironment.application;
        app.initData(DB_NAME);
        data = MainApplication.getData();
    }

    protected void clearCache() {
        app.closeData();
        data = app.initData(DB_NAME);
    }

    @After
    public void teardown() {
        if (app != null) {
            app.deleteData(DB_NAME);
        }
    }

    protected <T> Matcher<T> equalsNotSameInstance(T obj) {
        return allOf(equalTo(obj), not(sameInstance(obj)));
    }
}