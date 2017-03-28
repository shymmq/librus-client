package pl.librus.client.analytics;

/**
 * Created by robwys on 28/03/2017.
 */

import dagger.BindsOptionalOf;
import dagger.Module;

@Module
public abstract class AnalyticsModule {
    @BindsOptionalOf
    abstract IAnalytics optionalAnalytics();
}
