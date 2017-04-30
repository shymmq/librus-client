package pl.librus.client.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.amulyakhare.textdrawable.TextDrawable;
import com.google.common.base.Optional;
import com.google.common.collect.Ordering;
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
import java.util.Set;

import javax.inject.Inject;

import java8.util.stream.StreamSupport;
import pl.librus.client.MainApplication;
import pl.librus.client.R;
import pl.librus.client.domain.LuckyNumber;
import pl.librus.client.domain.Me;
import pl.librus.client.notification.RegistrationIntentService;
import pl.librus.client.presentation.FragmentPresenter;
import pl.librus.client.presentation.MainActivityPresenter;
import pl.librus.client.presentation.MainFragmentPresenter;
import pl.librus.client.presentation.SettingsPresenter;
import pl.librus.client.util.LibrusConstants;

public class MainActivity extends AppCompatActivity implements MainActivityOps, NavigationOps {
    public static final String INITIAL_FRAGMENT = "initial_fragment";

    List<? extends MenuAction> actions = new ArrayList<>();
    private Drawer drawer;
    private Toolbar toolbar;
    private Menu menu;
    private MaterialDialog progressDialog;

    @Inject
    MainActivityPresenter presenter;

    @Inject
    Set<MainFragmentPresenter> fragmentPresenters;

    @Inject
    SettingsPresenter settingsPresenter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean theme = prefs.getBoolean(getString(R.string.prefs_dark_theme), false);
        if (theme) {
            setTheme(R.style.AppTheme_Dark);
        } else {
            setTheme(R.style.AppTheme_Light);
        }

        setContentView(R.layout.activity_main);

        String login =  PreferenceManager.getDefaultSharedPreferences(this)
                .getString("login", null);
        if (login == null) {
            navigateToLogin();
            super.onCreate(savedInstanceState);
            return;
        } else {
            MainApplication.createMainActivityComponent(this)
                    .inject(this);
            presenter.setup();
            super.onCreate(savedInstanceState);
        }

    }

    @Override
    public void navigateToLogin() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    @Override
    public ProgressReporter displayProgressDialog() {
        progressDialog = new MaterialDialog.Builder(MainActivity.this)
                .title("Pobieranie danych")
                .content("")
                .progress(false, 100)
                .cancelable(false)
                .show();
        return new ProgressReporter(100, p -> progressDialog.setProgress(p));
    }

    @Override
    public void hideProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void showSnackBar(int message, int duration) {
        Snackbar.make(
                findViewById(R.id.activity_main_coordinator),
                message,
                duration)
                .show();
    }

    @Override
    public void setupDrawer(Me me, Optional<LuckyNumber> luckyNumber) {

        TextDrawable icon = getIcon(me);
        ProfileDrawerItem profile = getProfileDrawerItem(me, icon);

        DrawerBuilder drawerBuilder = new DrawerBuilder()
                .withActivity(this)
                .addStickyDrawerItems(settingsPresenter.convertToDrawerItem(this::displayFragment))
                .withDelayOnDrawerClose(50)
                .withOnDrawerNavigationListener(clickedView -> {
                    onBackPressed();
                    return true;
                })
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withToolbar(toolbar)
                .withAccountHeader(getDrawerHeader(profile));


        StreamSupport.stream(fragmentPresenters)
                .sorted(Ordering.natural().onResultOf(MainFragmentPresenter::getOrder))
                .map(presenter -> presenter.convertToDrawerItem(p -> displayFragment(presenter)))
                .forEach(drawerBuilder::addDrawerItems);

        if (luckyNumber.isPresent()) {
            drawerBuilder.addDrawerItems(
                    new DividerDrawerItem(),
                    getLuckyNumberDrawerItem(luckyNumber.get()));
        }

        this.drawer = drawerBuilder.build();
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

    private PrimaryDrawerItem getLuckyNumberDrawerItem(LuckyNumber luckyNumber) {
        return new PrimaryDrawerItem().withIconTintingEnabled(true).withSelectable(false)
                .withName(getString(R.string.lucky_number) + ": " + (luckyNumber == null ? 0 : luckyNumber.luckyNumber()))
                .withIcon(R.drawable.ic_sentiment_very_satisfied_black_24dp)
                .withOnDrawerItemClickListener(showLuckyNumber(luckyNumber));
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

    @Override
    public void setBackArrow(boolean enable) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            ActionBarDrawerToggle toggle = drawer.getActionBarDrawerToggle();
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
            presenter.luckyNumberClicked(luckyNumber);
            return true;
        };
    }

    private boolean logout(View view, int position, IDrawerItem drawerItem) {
        presenter.logout();
        return false;
    }

    @Override
    public Fragment getCurrentFragmentId() {
        return getSupportFragmentManager().findFragmentById(R.id.content_main);
    }

    @Override
    @Nullable
    public Integer getInitialFragmentTitle() {
        return getIntent().getIntExtra(INITIAL_FRAGMENT, -1);
    }

    @Override
    public void displayFragment(FragmentPresenter fragmentPresenter) {
        drawer.setSelection(fragmentPresenter.getTitle(), false);

        toolbar.setTitle(fragmentPresenter.getTitle());
        if (menu != null) {
            menu.clear();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_main, fragmentPresenter.getFragment())
                .commitAllowingStateLoss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return true;
    }

    @Override
    public void displayMenuActions(List<? extends MenuAction> actions) {
        this.actions = actions;
        updateMenu();
    }

    @Override
    public void updateMenu() {
        runOnUiThread(() -> {
            menu.clear();
            for (int id = 0; id < actions.size(); id++) {
                MenuAction action = actions.get(id);
                boolean enabled = action.isEnabled();
                MenuItem menuItem = menu.add(Menu.NONE, id, Menu.NONE, action.getName());
                menuItem.setEnabled(enabled);
                menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        actions.get(item.getItemId()).run();
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
        presenter.destroy();
        super.onDestroy();

        MainApplication.releaseMainActivityComponent();
    }

    @Override
    public void unregisterGCM() {
        Intent intent = new Intent(getApplicationContext(), RegistrationIntentService.class);
        intent.putExtra(LibrusConstants.REGISTER, false);
        startService(intent);
    }
}
