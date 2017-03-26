package pl.librus.client.db;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import io.reactivex.Single;
import io.requery.BlockingEntityStore;
import io.requery.Persistable;
import pl.librus.client.AnalyticsShadow;
import pl.librus.client.api.DatabaseStrategy;
import pl.librus.client.api.IAPIClient;
import pl.librus.client.api.LibrusData;
import pl.librus.client.ui.MainApplication;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;

@Config(application = MainApplication.class, shadows = AnalyticsShadow.class)
public abstract class BaseDBTest {

    public static final String DB_NAME = "test";
    protected BlockingEntityStore<Persistable> data;
    protected IAPIClient apiClient;
    protected LibrusData librusData;
    protected DatabaseStrategy databaseStrategy;


    @Before
    public void setup() {
        apiClient = Mockito.mock(IAPIClient.class);

        setupData();
    }

    private void setupData() {
        databaseStrategy = DatabaseStrategy.getInstance(RuntimeEnvironment.application, DB_NAME);

        librusData = LibrusData.getInstance(databaseStrategy, apiClient);
        data = databaseStrategy.getDataStore().toBlocking();
    }

    protected void clearCache() {
        DatabaseStrategy.close();
        setupData();
    }

    @After
    public void teardown() {
        DatabaseStrategy.delete(RuntimeEnvironment.application, DB_NAME);
    }

    protected <T> Matcher<T> equalsNotSameInstance(T obj) {
        return allOf(equalTo(obj), not(sameInstance(obj)));
    }
}