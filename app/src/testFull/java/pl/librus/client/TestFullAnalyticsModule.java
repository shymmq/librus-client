package pl.librus.client;

import org.mockito.Mockito;

import dagger.Module;
import pl.librus.client.analytics.Analytics;
import pl.librus.client.analytics.FullAnalyticsModule;
import pl.librus.client.analytics.IAnalytics;

/**
 * Created by robwys on 28/03/2017.
 */

@Module
public class TestFullAnalyticsModule extends FullAnalyticsModule {

    @Override
    protected IAnalytics provideAnalytics(Analytics analytics) {
        return Mockito.mock(Analytics.class);
    }
}
