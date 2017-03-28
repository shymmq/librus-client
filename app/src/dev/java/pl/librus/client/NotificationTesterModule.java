package pl.librus.client;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import pl.librus.client.presentation.MainFragmentPresenter;
import pl.librus.client.presentation.NotificationTesterPresenter;

/**
 * Created by robwys on 28/03/2017.
 */
@Module
public class NotificationTesterModule {
    @Provides
    @IntoSet
    MainFragmentPresenter provideNotificationTester(NotificationTesterPresenter presenter) {
        return presenter;
    }
}
