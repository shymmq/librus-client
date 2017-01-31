package pl.librus.client.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
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
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import pl.librus.client.LibrusUtils;
import pl.librus.client.R;
import pl.librus.client.api.LibrusUpdateService;
import pl.librus.client.api.LuckyNumber;
import pl.librus.client.attendances.AttendanceFragment;
import pl.librus.client.grades.GradesFragment;
import pl.librus.client.sql.LibrusDbContract;
import pl.librus.client.sql.LibrusDbHelper;
import pl.librus.client.timetable.TimetableFragment;
import pl.librus.client.timetable.TimetableTabFragment;

public class MainActivity extends AppCompatActivity implements Drawer.OnDrawerItemClickListener {
    public static final int FRAGMENT_TIMETABLE_ID = 1;
    public static final int FRAGMENT_GRADES_ID = 2;
    public static final int FRAGMENT_CALENDAR_ID = 3;
    public static final int FRAGMENT_ANNOUNCEMENTS_ID = 4;
    public static final int FRAGMENT_MESSAGES_ID = 5;
    public static final int FRAGMENT_ATTENDANCES_ID = 6;
    public static final int LUCKY_NUMBER_ID = 7;
    public static final int SETTINGS_ID = 8;

    private TimetableFragment timetableFragment = TimetableFragment.newInstance();
    private GradesFragment gradesFragment = GradesFragment.newInstance();
    private AttendanceFragment attendanceFragment = AttendanceFragment.newInstance();
    private TimetableTabFragment timetableTabFragment = TimetableTabFragment.newInstance();

    private SQLiteDatabase db;
    private LibrusDbHelper dbHelper;

    private ActionMenuView amv;
    private Drawer drawer;
    private Toolbar toolbar;

    private LibrusUpdateService updateService;

    private MainFragment currentFragment;
    private MainFragment pendingFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean Theme = prefs.getBoolean("selectTheme", false);
        if (Theme){
            setTheme(R.style.AppTheme_Dark);
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
            dbHelper = new LibrusDbHelper(this);
            db = dbHelper.getReadableDatabase();

            LibrusUpdateService updateService = new LibrusUpdateService(getApplicationContext());

            if (prefs.getLong(getString(R.string.last_update), -1) < 0) {
                //database empty or null update and then setup()

                final MaterialDialog dialog = new MaterialDialog.Builder(MainActivity.this)
                        .title("Pobieranie danych")
                        .content("")
                        .progress(false, 100)
                        .show();

                updateService.addOnProgressListener(new LibrusUpdateService.OnProgressListener() {
                    @Override
                    public void onProgress(final int progress) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LibrusUtils.log("Progress: " + progress + "%");
                                dialog.setProgress(progress);
                            }
                        });
                    }
                });
                updateService.setOnUpdateCompleteListener(new LibrusUpdateService.OnUpdateCompleteListener() {
                    @Override
                    public void onUpdateComplete() {
                        dialog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setup(savedInstanceState);
                            }
                        });
                    }
                });
                updateService.updateAll();
            } else {
                setup(savedInstanceState);
            }
        }
    }

    private void setup(Bundle savedInstanceState) {
        //LibrusAccount account = librusData.getAccount();
        //luckyNumber = librusData.getLuckyNumber();
        dbHelper = new LibrusDbHelper(this);
        db = dbHelper.getReadableDatabase();
        updateService = new LibrusUpdateService(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Drawer setup
        LuckyNumber luckyNumber = dbHelper.getLastLuckyNumber();
        Cursor cursor = db.query(
                LibrusDbContract.Account.TABLE_NAME,
                new String[]{
                        LibrusDbContract.Account.COLUMN_NAME_USERNAME,
                        LibrusDbContract.Account.COLUMN_NAME_FIRST_NAME,
                        LibrusDbContract.Account.COLUMN_NAME_LAST_NAME},
                null, null, null, null, null, null);
        String name, username;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            name = cursor.getString(cursor.getColumnIndexOrThrow(LibrusDbContract.Account.COLUMN_NAME_FIRST_NAME)) +
                    ' ' +
                    cursor.getString(cursor.getColumnIndexOrThrow(LibrusDbContract.Account.COLUMN_NAME_LAST_NAME));
            username = cursor.getString(cursor.getColumnIndexOrThrow(LibrusDbContract.Account.COLUMN_NAME_USERNAME));
        } else {
            name = "";
            username = "";
        }
        cursor.close();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        ProfileDrawerItem profile = new ProfileDrawerItem()
                .withName(name)
                .withEmail(username)
                .withIcon(R.drawable.ic_person_white_48px);
        PrimaryDrawerItem lucky = new PrimaryDrawerItem().withIconTintingEnabled(true).withSelectable(false)
                .withIdentifier(LUCKY_NUMBER_ID)
                .withName(getString(R.string.lucky_number) + ": " + (luckyNumber == null ? 0 : luckyNumber.getLuckyNumber()))
                .withIcon(R.drawable.ic_sentiment_very_satisfied_black_24dp);
        AccountHeader header = new AccountHeaderBuilder()
                .withActivity(this)
                .withSelectionListEnabledForSingleProfile(true)
                .withHeaderBackground(R.drawable.background_nav)
                .withHeaderBackgroundScaleType(ImageView.ScaleType.CENTER_CROP)
                .addProfiles(profile)
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

        //show the default fragment
        int id = Integer.parseInt(prefs.
                getString(
                        getString(R.string.prefs_default_fragment),
                        getString(R.string.timetable_view_key)
                ));
        final MainFragment defaultFragment = getFragmentForId(id);
        //when first fragment is set up, start the update
        defaultFragment.setOnSetupCompleteLister(new MainFragment.OnSetupCompleteListener() {
            @Override
            public void run() {
                updateService.updateAll();
                defaultFragment.removeListener();
            }
        });
        drawer.setSelection(id);
    }

    private void refresh() {

    }

    private Drawer getDrawer() {
        return drawer;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        for (int i = 0; i < toolbar.getChildCount(); ++i) {
            if (toolbar.getChildAt(i).getClass().getSimpleName().equals("ActionMenuView")) {
                amv = (ActionMenuView) toolbar.getChildAt(i);
                break;
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_sync:
                RotateAnimation r = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                r.setDuration(600);
                RotateAnimation rotateAnimation = new RotateAnimation(30, 90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setDuration(10000);
                amv.getChildAt(amv.getChildCount() - 1).startAnimation(r);
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //Regardless of update going on in the background start the settings activity
        int identifier = (int) drawerItem.getIdentifier();
        if (identifier == SETTINGS_ID) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return false;
        } else if (identifier == LUCKY_NUMBER_ID) {
            //TODO
        } else {
            if (updateService.isLoading()) {
                currentFragment = LoadingFragment.newInstance();
                pendingFragment = getFragmentForId(identifier);
                updateService.setOnUpdateCompleteListener(new LibrusUpdateService.OnUpdateCompleteListener() {
                    @Override
                    public void onUpdateComplete() {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        if (pendingFragment != null)
                            transaction
                                    .replace(R.id.content_main, (Fragment) pendingFragment)
                                    .commit();
                    }
                });
            } else {
                currentFragment = getFragmentForId(identifier);
            }
            transaction
                    .replace(R.id.content_main, (Fragment) currentFragment)
                    .commit();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (drawer != null) {
            outState = drawer.saveInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
    }
}
