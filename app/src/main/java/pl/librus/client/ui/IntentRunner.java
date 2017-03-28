package pl.librus.client.ui;

import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.librus.client.notification.RegistrationIntentService;
import pl.librus.client.util.LibrusConstants;

/**
 * Created by robwys on 28/03/2017.
 */

@Singleton
public class IntentRunner {

    private final Context context;

    @Inject
    public IntentRunner(Context context) {
        this.context = context;
    }

    public void runRegistrationService(boolean register) {
        Intent intent = new Intent(context, RegistrationIntentService.class);
        intent.putExtra(LibrusConstants.REGISTER, register);
        context.startService(intent);
    }

    public void navigateToLogin() {
        Intent i = new Intent(context, LoginActivity.class);
        context.startActivity(i);
    }
}
