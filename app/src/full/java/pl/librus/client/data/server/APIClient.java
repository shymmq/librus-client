package pl.librus.client.data.server;

import android.content.Context;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by szyme on 14.02.2017.
 */
@Singleton
public class APIClient extends DefaultAPIClient implements IAPIClient{

    @Inject
    public APIClient(Context _context) {
        super(_context);
    }
}
