package pl.librus.client;

import dagger.Subcomponent;
import pl.librus.client.notification.LibrusGcmListenerService;

/**
 * Created by robwys on 27/03/2017.
 */
@UserScope
@Subcomponent(modules = UserModule.class)
public interface UserComponent {
    MainActivityComponent plus(MainActivityModule userModule);

    void inject(LibrusGcmListenerService service);
}
