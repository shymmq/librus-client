package pl.librus.client.db;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import io.requery.BlockingEntityStore;
import io.requery.Persistable;
import pl.librus.client.TestApplication;
import pl.librus.client.data.ServerFallbackStrategy;
import pl.librus.client.data.db.DatabaseManager;
import pl.librus.client.data.server.APIClient;
import pl.librus.client.data.LibrusData;
import pl.librus.client.MainApplication;
import pl.librus.client.data.server.IAPIClient;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;

@Config(application = TestApplication.class)
@RunWith(RobolectricTestRunner.class)
public abstract class BaseDBTest {

    public static final String DB_NAME = "test";
    protected BlockingEntityStore<Persistable> data;
    protected IAPIClient apiClient;
    protected LibrusData librusData;
    protected DatabaseManager databaseManager;

    @Before
    public void setup() {
        apiClient = Mockito.mock(IAPIClient.class);

        setupData();
    }

    private void setupData() {
        databaseManager = new DatabaseManager(RuntimeEnvironment.application, DB_NAME);

        ServerFallbackStrategy serverFallbackStrategy = new ServerFallbackStrategy(apiClient, databaseManager);

        librusData = new LibrusData(serverFallbackStrategy);
        data = databaseManager.getDataStore().toBlocking();
    }

    protected void clearCache() {
        setupData();
    }

    @After
    public void teardown() {
        databaseManager.delete();
    }

    protected <T> Matcher<T> equalsNotSameInstance(T obj) {
        return allOf(equalTo(obj), not(sameInstance(obj)));
    }
}