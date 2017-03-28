package pl.librus.client;

import javax.inject.Singleton;

import dagger.Component;
import pl.librus.client.analytics.AnalyticsModule;

/**
 * Created by robwys on 28/03/2017.
 */

@Singleton
@Component(modules = {
        ApplicationModule.class,
        AnalyticsModule.class
})
public interface ApplicationComponent extends BaseApplicationComponent {
}
