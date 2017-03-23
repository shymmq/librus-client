package pl.librus.client.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amulyakhare.textdrawable.TextDrawable;
import com.google.common.base.Optional;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import java8.util.stream.StreamSupport;
import pl.librus.client.BuildConfig;
import pl.librus.client.LibrusConstants;
import pl.librus.client.LibrusUtils;
import pl.librus.client.R;
import pl.librus.client.api.DrawerData;
import pl.librus.client.api.HttpException;
import pl.librus.client.api.ImmutableDrawerData;
import pl.librus.client.api.LibrusData;
import pl.librus.client.api.MaintenanceException;
import pl.librus.client.api.OfflineException;
import pl.librus.client.api.ProgressReporter;
import pl.librus.client.api.RegistrationIntentService;
import pl.librus.client.datamodel.LuckyNumber;
import pl.librus.client.datamodel.Me;
import pl.librus.client.sql.UpdateHelper;
import pl.librus.client.timetable.TimetableFragment;

public class MainActivity extends AppCompatActivity {
    public static final String INITIAL_FRAGMENT = "initial_fragment";

    List<? extends MenuAction> actions = new ArrayList<>();
    private Drawer drawer;
    private Toolbar toolbar;
    private Menu menu;
    private MaterialDialog progressDialog;
    private MainFragment currentFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean theme = prefs.getBoolean(getString(R.string.prefs_dark_theme), false);
        if (theme) {
            setTheme(R.style.AppTheme_Dark);
        } else {
            setTheme(R.style.AppTheme_Light);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String login = prefs.getString("login", null);
        if (login == null) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            finish();
        } else {
            if (!wasAlreadyOpen()) {
                //database empty or null update and then setup()

                progressDialog = new MaterialDialog.Builder(MainActivity.this)
                        .title("Pobieranie danych")
                        .content("")
                        .progress(false, 100)
                        .cancelable(false)
                        .show();
                ProgressReporter reporter = new ProgressReporter(100, p -> runOnUiThread(() -> progressDialog.setProgress(p)));
                new UpdateHelper(LibrusData.getInstance(this)).updateAll(reporter)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                reporter::report,
                                this::handleUpdateError,
                                this::setup);
            } else {
                setup();
            }
        }
    }

    private void handleUpdateError(Throwable exception) {
        LibrusUtils.log("Handle update error");
        if (exception instanceof OfflineException) {
            LibrusUtils.log("Offline mode");
            setup();
            Snackbar.make(
                    findViewById(R.id.activity_main_coordinator),
                    R.string.offline_data_error,
                    Snackbar.LENGTH_LONG)
                    .show();
        } else if (exception instanceof HttpException && exception.getMessage().contains("Request is denied")) {
            LibrusUtils.log("Request denied, logout");

            //User probably changed password
            logout();
        } else {
            LibrusUtils.logError("Unknown error");
            exception.printStackTrace();
            Snackbar.make(
                    findViewById(R.id.activity_main_coordinator),
                    R.string.unknown_error,
                    Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    private void setup() {
        LibrusUtils.log("setting up");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        Single
                .zip(
                        LibrusData.getInstance(this).findMe(),
                        LibrusData.getInstance(this).findLuckyNumber(),
                        ImmutableDrawerData::of)
                .observeOn(AndroidSchedulers.mainThread())
                .map(this::initDrawer)
                .subscribe(drawer -> {
                    this.drawer = drawer;
                    displayInitialFragment();
                });
    }

    private Drawer initDrawer(DrawerData drawerData) {
        TextDrawable icon = getIcon(drawerData.me());
        ProfileDrawerItem profile = getProfileDrawerItem(drawerData.me(), icon);
        AccountHeader header = getDrawerHeader(profile);

        final DrawerBuilder drawerBuilder = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(header)
                .addDrawerItems(new DrawerItemsFactory().getItems(this::displayFragment));

        if (drawerData.luckyNumber().isPresent()) {
            drawerBuilder.addDrawerItems(new DividerDrawerItem(), getLuckyNumberDrawerItem(drawerData.luckyNumber()));
        }

        drawerBuilder.addStickyDrawerItems(getSettingsDrawerItem())
                .withDelayOnDrawerClose(50)
                .withOnDrawerNavigationListener(clickedView -> {
                    onBackPressed();
                    return true;
                })
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withToolbar(toolbar);
        return drawerBuilder.build();
    }

    private PrimaryDrawerItem getSettingsDrawerItem() {
        SettingsFragment fragment = new SettingsFragment();
        return new PrimaryDrawerItem().withIconTintingEnabled(true).withSelectable(false)
                .withName(fragment.getTitle())
                .withIcon(fragment.getIcon())
                .withIdentifier(fragment.getTitle())
                .withIconTintingEnabled(true)
                .withOnDrawerItemClickListener(this::openSettings);
    }

    private AccountHeader getDrawerHeader(ProfileDrawerItem profile) {
        return new AccountHeaderBuilder()
                .withActivity(this)
                .withSelectionListEnabledForSingleProfile(true)
                .withHeaderBackground(R.drawable.background_nav)
                .withHeaderBackgroundScaleType(ImageView.ScaleType.CENTER_CROP)
                .addProfiles(profile,
                        //TODO: Add  support for multi profiles
                        new ProfileSettingDrawerItem()
                                .withName("Wyloguj się")
                                .withIcon(R.drawable.logout)
                                .withOnDrawerItemClickListener(this::logout))
                .build();
    }

    private PrimaryDrawerItem getLuckyNumberDrawerItem(Optional<LuckyNumber> luckyNumber) {
        return new PrimaryDrawerItem().withIconTintingEnabled(true).withSelectable(false)
                .withName(getString(R.string.lucky_number) + ": " + (luckyNumber == null ? 0 : luckyNumber.get().luckyNumber()))
                .withIcon(R.drawable.ic_sentiment_very_satisfied_black_24dp)
                .withOnDrawerItemClickListener(showLuckyNumber(luckyNumber.get()));
    }

    private ProfileDrawerItem getProfileDrawerItem(Me me, TextDrawable icon) {
        return new ProfileDrawerItem()
                .withName(me.account().name())
                .withEmail(me.account().login())
                .withIcon(icon);
    }

    private TextDrawable getIcon(Me me) {
        return TextDrawable.builder()
                .beginConfig()
                .height(48)
                .width(48)
                .endConfig()
                .buildRect(me.account().firstName().substring(0, 1), Color.parseColor("#F49719"));
    }

    private Drawer getDrawer() {
        return drawer;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setBackArrow(boolean enable) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            ActionBarDrawerToggle toggle = getDrawer().getActionBarDrawerToggle();
            if (enable) {
                toggle.setDrawerIndicatorEnabled(false);
                actionBar.setDisplayHomeAsUpEnabled(true);
            } else {
                actionBar.setDisplayHomeAsUpEnabled(false);
                toggle.setDrawerIndicatorEnabled(true);
            }
            toggle.syncState();
        }
    }

    private Drawer.OnDrawerItemClickListener showLuckyNumber(LuckyNumber luckyNumber) {
        return (v, p, di) -> {
            if (luckyNumber != null) {
                String luckyDate = luckyNumber.day().toString("EEEE, d MMMM");
                Toast.makeText(getApplicationContext(), luckyDate, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Brak danych", Toast.LENGTH_LONG).show();
            }
            return true;
        };
    }

    private boolean openSettings(View view, int position, IDrawerItem drawerItem) {
        displayFragment(new SettingsFragment());
        return false;
    }

    private boolean logout(View view, int position, IDrawerItem drawerItem) {
        logout();
        return false;
    }

    private void logout() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String login = prefs.getString("login", null);
        if (login != null) {
            prefs.edit()
                    .clear()
                    .apply();

            LibrusData.delete(getApplicationContext(), login);

            disableNotifications();

            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);

            Toast.makeText(MainActivity.this, "Wylogowano", Toast.LENGTH_SHORT).show();

            finish();
        }
    }

    private void disableNotifications() {
        Intent intent = new Intent(getApplicationContext(), RegistrationIntentService.class);
        intent.putExtra(LibrusConstants.REGISTER, false);
        startService(intent);
    }

    private boolean wasAlreadyOpen() {
        return getSupportFragmentManager().findFragmentById(R.id.content_main) != null;
    }

    private void displayInitialFragment() {
        if(!wasAlreadyOpen()) {
            displayFragment(getInitialFragment());
        }
    }

    private BaseFragment getInitialFragment() {
        int fragmentTitle = getIntent().getIntExtra(INITIAL_FRAGMENT, -1);

        if (fragmentTitle < 0) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String defaultFragment = prefs.getString("defaultFragment", "-1");

            fragmentTitle = Integer.valueOf(defaultFragment);
        }
        return getFragmentForTitle(fragmentTitle);
    }

    private BaseFragment getFragmentForTitle(int fragmentTitle) {
        return StreamSupport.stream(new FragmentsRepository().getAll())
                .filter(f -> f.getTitle() == fragmentTitle)
                .findFirst()
                .orElseGet(TimetableFragment::new);
    }

    private <T extends Fragment & MainFragment> void displayFragment(T fragment) {
        drawer.setSelection(fragment.getTitle(), false);

        getToolbar().setTitle(fragment.getTitle());

        menu.clear();
        this.currentFragment = fragment;
        fragment.setMenuActionsHandler(this::setActions);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_main, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return true;
    }

    public void setActions(List<? extends MenuAction> actions) {
        this.actions = actions;
        updateMenu();
    }

    private void updateMenu() {
        for (int id = 0; id < actions.size(); id++) {
            MenuAction action = actions.get(id);
            boolean enabled = action.isEnabled();
            MenuItem menuItem = menu.add(Menu.NONE, id, Menu.NONE, action.getName());
            menuItem.setEnabled(enabled);
            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        actions.get(item.getItemId()).run();
        menu.clear();
        updateMenu();
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (drawer != null) {
            outState = drawer.saveInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LibrusData.close();
    }
}
