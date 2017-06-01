package pl.librus.client;

import dagger.Subcomponent;
import pl.librus.client.ui.NotificationTesterFragment;

/**
 * Created by robwys on 28/03/2017.
 */

@MainActivityScope
@Subcomponent(modules = {
        MainActivityModule.class,
        NotificationTesterModule.class
})
public interface MainActivityComponent extends BaseMainActivityComponent {
    void inject(NotificationTesterFragment fragment);
}

