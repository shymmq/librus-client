package pl.librus.client.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.google.firebase.analytics.FirebaseAnalytics;
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

import io.requery.Persistable;
import io.requery.sql.EntityDataStore;
import pl.librus.client.BuildConfig;
import pl.librus.client.LibrusUtils;
import pl.librus.client.R;
import pl.librus.client.api.ProgressReporter;
import pl.librus.client.attendances.AttendanceFragment;
import pl.librus.client.datamodel.LuckyNumber;
import pl.librus.client.datamodel.LuckyNumberType;
import pl.librus.client.datamodel.Me;
import pl.librus.client.grades.GradesFragment;
import pl.librus.client.sql.UpdateHelper;
import pl.librus.client.timetable.TimetableFragment;
import pl.librus.client.timetable.TimetableTabFragment;

public class MainActivity extends AppCompatActivity implements Drawer.OnDrawerItemClickListener {
    public static final int FRAGMENT_GRADES_ID = 2;
    public static final int FRAGMENT_ANNOUNCEMENTS_ID = 4;

    private static final int FRAGMENT_TIMETABLE_ID = 1;
    private static final int FRAGMENT_CALENDAR_ID = 3;
    private static final int FRAGMENT_MESSAGES_ID = 5;
    private static final int FRAGMENT_ATTENDANCES_ID = 6;
    private static final int LUCKY_NUMBER_ID = 7;
    private static final int SETTINGS_ID = 8;
    private static final int PROFILE_SETTING = 9;
    private static final int PROFILE_SETTING_LOGOUT = 10;

    private final TimetableFragment timetableFragment = TimetableFragment.newInstance();
    private final GradesFragment gradesFragment = GradesFragment.newInstance();
    private final AttendanceFragment attendanceFragment = AttendanceFragment.newInstance();
    private final TimetableTabFragment timetableTabFragment = TimetableTabFragment.newInstance();
    MainFragment currentFragment;
    List<? extends MenuAction> actions = new ArrayList<>();
    private Drawer drawer;
    private Toolbar toolbar;
    private UpdateHelper updateHelper;
    private Menu menu;
    private EntityDataStore<Persistable> data;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        MainApplication app = (MainApplication) getApplicationContext();
        data = app.initData();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean theme = prefs.getBoolean(getString(R.string.prefs_dark_theme), false);
        if (theme) {
            setTheme(R.style.AppTheme_Dark);
        } else {
            setTheme(R.style.AppTheme_Light);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAnalytics.getInstance(getApplicationContext());


        boolean logged_in = prefs.getBoolean("logged_in", false);
        if (!logged_in) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            finish();
        } else {

            UpdateHelper updateHelper = new UpdateHelper(getApplicationContext());

            if (BuildConfig.DEBUG || prefs.getLong(getString(R.string.last_update), -1) < 0) {
                //database empty or null update and then setup()

                final MaterialDialog dialog = new MaterialDialog.Builder(MainActivity.this)
                        .title("Pobieranie danych")
                        .content("")
                        .progress(false, 100)
                        .show();
                ProgressReporter reporter = new ProgressReporter(100, p -> runOnUiThread(() -> dialog.setProgress(p)));
                updateHelper.updateAll(reporter)
                        .whenComplete((result, exception) -> {
                            if(exception != null) {
                                //TODO: better error handling
                                LibrusUtils.logError(exception.toString());
                            } else{
                                dialog.dismiss();
                                runOnUiThread(this::setup);
                            }
                        });
            } else {
                setup();
            }
        }
    }

    private void setup() {
        LibrusUtils.log("setting up");
        updateHelper = new UpdateHelper(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Drawer setup

        Me me = data.select(Me.class).get().first();

        LuckyNumber luckyNumber = data.select(LuckyNumber.class)
                .orderBy(LuckyNumberType.DAY.desc())
                .get()
                .firstOrNull();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        ProfileDrawerItem profile = new ProfileDrawerItem()
                .withName(me.account().name())
                .withEmail(me.account().login())
                .withIcon(R.drawable.ic_person_white_48px);
        PrimaryDrawerItem lucky = new PrimaryDrawerItem().withIconTintingEnabled(true).withSelectable(false)
                .withIdentifier(LUCKY_NUMBER_ID)
                .withName(getString(R.string.lucky_number) + ": " + (luckyNumber == null ? 0 : luckyNumber.luckyNumber()))
                .withIcon(R.drawable.ic_sentiment_very_satisfied_black_24dp);
        AccountHeader header = new AccountHeaderBuilder()
                .withActivity(this)
                .withSelectionListEnabledForSingleProfile(true)
                .withHeaderBackground(R.drawable.background_nav)
                .withHeaderBackgroundScaleType(ImageView.ScaleType.CENTER_CROP)
                .addProfiles(profile,
                        new ProfileSettingDrawerItem().withName("Dodaj konto").withIdentifier(PROFILE_SETTING).withIcon(R.drawable.plus),
                        //TODO: Add  support for multi profiles
                        new ProfileSettingDrawerItem().withName("Wyloguj").withIdentifier(PROFILE_SETTING_LOGOUT).withIcon(R.drawable.logout).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = prefs.edit();
                                boolean logged_in = prefs.getBoolean("logged_in", true);
                                if (logged_in) {
                                    Toast.makeText(MainActivity.this, "Wylogowano", Toast.LENGTH_SHORT).show();
                                    editor.clear();
                                    editor.apply();
                                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                                return false;
                            }
                        }))
                .build();

        final DrawerBuilder drawerBuilder = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIconTintingEnabled(true)
                                .withIdentifier(FRAGMENT_TIMETABLE_ID)
                                .withName(R.string.timetable_view_title)
                                .withIcon(R.drawable.ic_event_note_black_48dp),
                        new PrimaryDrawerItem().withIconTintingEnabled(true)
                                .withIdentifier(FRAGMENT_GRADES_ID)
                                .withName(R.string.grades_view_title)
                                .withIcon(R.drawable.ic_assignment_black_48dp),
                        new PrimaryDrawerItem().withIconTintingEnabled(true)
                                .withIdentifier(FRAGMENT_CALENDAR_ID)
                                .withName(R.string.calendar_view_title)
                                .withIcon(R.drawable.ic_date_range_black_48dp),
                        new PrimaryDrawerItem().withIconTintingEnabled(true)
                                .withIdentifier(FRAGMENT_ANNOUNCEMENTS_ID)
                                .withName(R.string.announcements_view_title)
                                .withIcon(R.drawable.ic_announcement_black_48dp),
                        new PrimaryDrawerItem().withIconTintingEnabled(true)
                                .withIdentifier(FRAGMENT_MESSAGES_ID)
                                .withName(R.string.messages_view_title)
                                .withIcon(R.drawable.ic_message_black_48dp),
                        new PrimaryDrawerItem().withIconTintingEnabled(true)
                                .withIdentifier(FRAGMENT_ATTENDANCES_ID)
                                .withName(R.string.attendances_view_title)
                                .withIcon(R.drawable.ic_person_outline_black_48dp),
                        new DividerDrawerItem(),
                        lucky)
                .addStickyDrawerItems(new PrimaryDrawerItem().withIconTintingEnabled(true).withSelectable(false)
                        .withIdentifier(SETTINGS_ID)
                        .withName(R.string.settings_title)
                        .withIcon(R.drawable.ic_settings_black_48dp))
                .withOnDrawerItemClickListener(this)
                .withDelayOnDrawerClose(50)
                .withOnDrawerNavigationListener(new Drawer.OnDrawerNavigationListener() {
                    @Override
                    public boolean onNavigationClickListener(View clickedView) {
                        onBackPressed();
                        return true;
                    }
                })
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withToolbar(toolbar);
        drawer = drawerBuilder.build();
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

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        //Regardless of update going on in the background start the settings activity
        int itemId = (int) drawerItem.getIdentifier();
        if (itemId == SETTINGS_ID) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return false;
        } else if (itemId == LUCKY_NUMBER_ID) {
            LuckyNumber luckyNumber = data.select(LuckyNumber.class)
                    .orderBy(LuckyNumberType.DAY.desc())
                    .get()
                    .firstOrNull();
            if (luckyNumber != null) {
                String luckyDate = luckyNumber.day().toString("EEEE, d MMMM");
                Toast.makeText(getApplicationContext(), luckyDate, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Brak danych", Toast.LENGTH_LONG).show();
            }
        } else {
            //Set toolbar title to clicked frawer item title
            getToolbar().setTitle(getTitleForId(itemId));
            currentFragment = getFragmentForId(itemId);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_main, currentFragment)
                    .commit();
            currentFragment.setOnSetupCompleteListener(new MainFragment.OnSetupCompleteListener() {
                @Override
                public void run() {
                    updateMenu();
                    currentFragment.removeListener();
                }
            });
        }
        return false;
    }


    private MainFragment getFragmentForId(int id) {
        MainFragment result;
        switch (id) {
            case FRAGMENT_TIMETABLE_ID:
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                result = preferences.getBoolean("useTabs", false) ? timetableTabFragment : timetableFragment;
                break;
            case FRAGMENT_GRADES_ID:
                result = gradesFragment;
                break;
            case FRAGMENT_ATTENDANCES_ID:
                result = attendanceFragment;
                break;
            default:
                result = new PlaceholderFragment();
        }
        return result;
    }

    private String getTitleForId(int id) {
        switch (id) {
            case FRAGMENT_TIMETABLE_ID:
                return getString(R.string.timetable_view_title);
            case FRAGMENT_GRADES_ID:
                return getString(R.string.grades_view_title);
            case FRAGMENT_ATTENDANCES_ID:
                return getString(R.string.attendances_view_title);
            default:
                return getString(R.string.app_name);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        //show the default fragment
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int id = Integer.parseInt(prefs.
                getString(
                        getString(R.string.prefs_default_fragment),
                        getString(R.string.timetable_view_key)
                ));
        final MainFragment defaultFragment = getFragmentForId(id);
        //when first fragment is set up, start the update
        defaultFragment.setOnSetupCompleteListener(new MainFragment.OnSetupCompleteListener() {
            @Override
            public void run() {
                defaultFragment.removeListener();
            }
        });
        drawer.setSelection(id);
        return true;
    }

    private void updateMenu() {
        actions = currentFragment.getMenuItems();
        menu.clear();
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
        MainApplication app = (MainApplication) getApplicationContext();
        app.closeData();
    }
}
