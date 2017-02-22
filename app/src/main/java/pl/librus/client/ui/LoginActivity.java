package pl.librus.client.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;

import pl.librus.client.R;
import pl.librus.client.api.APIClient;
import pl.librus.client.api.HttpException;
import pl.librus.client.api.RegistrationIntentService;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "librus-schedule-debug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);

        setContentView(R.layout.activity_login);

        final EditText passwordInput = (EditText) findViewById(R.id.password_input);
        final EditText usernameInput = (EditText) findViewById(R.id.username_input);
        final Button loginButton = (Button) findViewById(R.id.login_btn);
        final ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setVisibility(View.INVISIBLE);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        loginButton.setOnClickListener(v -> {
            //log in normally
            String username = usernameInput.getText().toString();
            String password = passwordInput.getText().toString();
            new APIClient(getApplicationContext()).login(username, password)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            String message = "Wystąpił niespodziewany błąd";
                            if (exception.getCause() instanceof HttpException) {
                                if (exception.getCause().getMessage().contains("invalid_grant")) {
                                    message = "Nieprawidłowe hasło, spróbuj ponownie";
                                }
                            }
                            Snackbar snackbar = Snackbar
                                    .make(findViewById(R.id.coordinator), message, Snackbar.LENGTH_SHORT);

                            snackbar.show();
                            Log.d(TAG, "onUpdateComplete: login failure ");
                        } else {
                            sharedPreferences
                                    .edit()
                                    .putBoolean("logged_in", true)
                                    .apply();
                            registerGCM();
                            showMainActivity();
                        }
                        runOnUiThread(() -> progress.setVisibility(View.INVISIBLE));
                    });
            progress.setVisibility(View.VISIBLE);
        });

    }

    private void showMainActivity() {
        Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent1);
        finish();
    }

    private void registerGCM() {
        Intent intent2 = new Intent(getApplicationContext(), RegistrationIntentService.class);
        startService(intent2);
    }

}
