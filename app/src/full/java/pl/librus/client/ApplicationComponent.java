package pl.librus.client;

import javax.inject.Singleton;

import dagger.Component;
import pl.librus.client.analytics.AnalyticsModule;
import pl.librus.client.analytics.FullAnalyticsModule;

/**
 * Created by robwys on 28/03/2017.
 */

@Singleton
@Component(modules = {
        ApplicationModule.class,
        AnalyticsModule.class,
        FullAnalyticsModule.class
})
public interface ApplicationComponent extends BaseApplicationComponent {
}
