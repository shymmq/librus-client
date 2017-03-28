package pl.librus.client;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by robwys on 27/03/2017.
 */

@Module
public class UserModule {
    private final String login;

    public UserModule(String login) {
        this.login = login;
    }

    @Provides
    @Named("login")
    String provideLogin() {
        return login;
    }
}
