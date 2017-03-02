package pl.librus.client.db;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import io.requery.BlockingEntityStore;
import io.requery.Persistable;
import pl.librus.client.api.IAPIClient;
import pl.librus.client.api.LibrusData;
import pl.librus.client.ui.MainApplication;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;

@Config(application = MainApplication.class)
public abstract class BaseDBTest {

    public static final String DB_NAME = "test";
    protected BlockingEntityStore<Persistable> data;
    protected IAPIClient apiClient;

    @Before
    public void setup() {
        apiClient = Mockito.mock(IAPIClient.class);
        LibrusData.init(RuntimeEnvironment.application, apiClient, DB_NAME);
        data = LibrusData.getDataStore().toBlocking();
    }

    protected void clearCache() {
        LibrusData.close();
        data = LibrusData.init(RuntimeEnvironment.application, apiClient, DB_NAME).toBlocking();
    }

    @After
    public void teardown() {
        LibrusData.delete(RuntimeEnvironment.application, DB_NAME);

    }

    protected <T> Matcher<T> equalsNotSameInstance(T obj) {
        return allOf(equalTo(obj), not(sameInstance(obj)));
    }
}