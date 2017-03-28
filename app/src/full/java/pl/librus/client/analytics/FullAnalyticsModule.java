package pl.librus.client.analytics;

import dagger.Module;
import dagger.Provides;

/**
 * Created by robwys on 28/03/2017.
 */

@Module
public class FullAnalyticsModule {
    @Provides
    protected IAnalytics provideAnalytics(Analytics analytics) {
        return analytics;
    }
}
