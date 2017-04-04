package pl.librus.client.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import pl.librus.client.MainApplication;
import pl.librus.client.R;
import pl.librus.client.data.server.APIClient;
import pl.librus.client.data.server.HttpException;
import pl.librus.client.data.server.OfflineException;
import pl.librus.client.notification.RegistrationIntentService;
import pl.librus.client.util.LibrusConstants;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "librus-schedule-debug";
    private ProgressBar progress;

    @Inject
    APIClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.component().inject(this);
        setTheme(R.style.AppTheme);

        setContentView(R.layout.activity_login);

        final EditText passwordInput = (EditText) findViewById(R.id.password_input);
        final EditText usernameInput = (EditText) findViewById(R.id.username_input);
        final Button loginButton = (Button) findViewById(R.id.login_btn);
        TextView forgotPassView = (TextView) findViewById(R.id.forgot_password);
        forgotPassView.setMovementMethod(LinkMovementMethod.getInstance());
        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setVisibility(View.INVISIBLE);

        loginButton.setOnClickListener(v -> {
            //log in normally
            String username = usernameInput.getText().toString();
            String password = passwordInput.getText().toString();
            apiClient.login(username, password)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            this::loginSuccessful,
                            this::loginFailed);
        });

    }

    private void loginSuccessful(String username) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putString("login", username)
                .apply();
        registerGCM();
        showMainActivity();
    }

    private void loginFailed(Throwable exception) {
        int message;
        if(exception instanceof HttpException && exception.getMessage().contains("invalid_grant")){
            message = R.string.wrong_credentials;
        } else if(exception instanceof OfflineException){
            message = R.string.offline_error;
        } else {
            message = R.string.unknown_error;
        }
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.coordinator), message, Snackbar.LENGTH_SHORT);

        snackbar.show();
        Log.d(TAG, "onUpdateComplete: login failure ");

        progress.setVisibility(View.INVISIBLE);

    }

    private void showMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void registerGCM() {
        Intent intent = new Intent(getApplicationContext(), RegistrationIntentService.class);
        intent.putExtra(LibrusConstants.REGISTER, true);
        startService(intent);
    }

}
