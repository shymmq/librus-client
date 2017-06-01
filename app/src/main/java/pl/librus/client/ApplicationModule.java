package pl.librus.client;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import pl.librus.client.data.server.APIClient;
import pl.librus.client.data.server.IAPIClient;

@Module
public class ApplicationModule {
    private final MainApplication mApplication;

    public ApplicationModule(MainApplication app) {
        mApplication = app;
    }

    @Provides
    Context provideContext() {
        return mApplication;
    }

    @Provides
    IAPIClient provideAPIClient(APIClient apiClient) {
        return apiClient;
    }
}
