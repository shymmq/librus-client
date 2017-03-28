package pl.librus.client;

/**
 * Created by robwys on 28/03/2017.
 */

public class TestApplication extends MainApplication {

    @Override
    protected ApplicationComponent buildApplicationComponent() {
        return DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .fullAnalyticsModule(new TestFullAnalyticsModule())
                .build();
    }
}
