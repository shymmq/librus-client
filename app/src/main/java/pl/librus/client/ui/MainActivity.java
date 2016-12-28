package pl.librus.client.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.jdeferred.DoneCallback;
import org.jdeferred.android.AndroidDoneCallback;
import org.jdeferred.android.AndroidExecutionScope;
import org.jdeferred.android.AndroidFailCallback;

import java.util.Locale;

import pl.librus.client.R;
import pl.librus.client.announcements.AnnouncementsFragment;
import pl.librus.client.api.LibrusAccount;
import pl.librus.client.api.LibrusData;
import pl.librus.client.api.LibrusDataLoader;
import pl.librus.client.api.LuckyNumber;
import pl.librus.client.api.NotificationService;
import pl.librus.client.attendances.AttendanceFragment;
import pl.librus.client.grades.GradesFragment;
import pl.librus.client.timetable.TimetableFragment;

public class MainActivity extends AppCompatActivity {
    public static final int FRAGMENT_TIMETABLE_ID = 0;
    public static final int FRAGMENT_GRADES_ID = 1;
    public static final int FRAGMENT_ANNOUNCEMENTS_ID = 3;
    private static final int FRAGMENT_CALENDAR_ID = 2;
    private final String TAG = "librus-client-log";
    private LuckyNumber luckyNumber;
    private ActionMenuView amv;
    private AppBarLayout appBarLayout;
    private LibrusData librusData;
    private Drawer drawer;
    private Toolbar toolbar;
    private Fragment currentFragment;
    MainFragment.OnSetupCompleteListener refreshListener = new MainFragment.OnSetupCompleteListener() {
        @Override
        public void onSetupComplete() {
            refresh();
        }
    };
    private View toolbarView;
    private AttendanceFragment attendanceFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseAnalytics.getInstance(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean logged_in = prefs.getBoolean("logged_in", false);
        if (!logged_in) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        } else {
            LibrusDataLoader.load(this).done(new AndroidDoneCallback<LibrusData>() {
                @Override
                public AndroidExecutionScope getExecutionScope() {
                    return null;
                }

                @Override
                public void onDone(LibrusData result) {
                    librusData = result;
                    setup();
                }
            }).fail(new AndroidFailCallback<Object>() {
                @Override
                public AndroidExecutionScope getExecutionScope() {
                    return null;
                }

                @Override
                public void onFail(Object result) {
                    librusData = new LibrusData(MainActivity.this);
                    LibrusDataLoader.updatePersistent(librusData, MainActivity.this).done(new DoneCallback<LibrusData>() {
                        @Override
                        public void onDone(LibrusData result) {
                            LibrusDataLoader.save(result, getApplicationContext());
                            setup();
                        }
                    });
                }
            });
        }
    }

    private void setup() {
        LibrusAccount account = librusData.getAccount();
        luckyNumber = librusData.getLuckyNumber();

        //Drawer setup
        ProfileDrawerItem profile = new ProfileDrawerItem().withName(account.getName()).withEmail(account.getEmail()).withIcon(R.drawable.jeb);
        PrimaryDrawerItem lucky = new PrimaryDrawerItem().withIconTintingEnabled(true).withSelectable(false)
                .withIdentifier(666)
                .withName("Szczęśliwy numerek: " + luckyNumber.getLuckyNumber())
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
                                .withName("Plan lekcji")
                                .withIcon(R.drawable.ic_event_note_black_48dp),
                        new PrimaryDrawerItem().withIconTintingEnabled(true)
                                .withIdentifier(FRAGMENT_GRADES_ID)
                                .withName("Oceny")
                                .withIcon(R.drawable.ic_assignment_black_48dp),
                        new PrimaryDrawerItem().withIconTintingEnabled(true)
                                .withIdentifier(FRAGMENT_CALENDAR_ID)
                                .withName("Terminarz")
                                .withIcon(R.drawable.ic_date_range_black_48dp),
                        new PrimaryDrawerItem().withIconTintingEnabled(true)
                                .withIdentifier(FRAGMENT_ANNOUNCEMENTS_ID)
                                .withName("Ogłoszenia")
                                .withIcon(R.drawable.ic_announcement_black_48dp),
                        new PrimaryDrawerItem().withIconTintingEnabled(true)
                                .withIdentifier(4)
                                .withName("Wiadomości")
                                .withIcon(R.drawable.ic_message_black_48dp),
                        new PrimaryDrawerItem().withIconTintingEnabled(true)
                                .withIdentifier(5)
                                .withName("Nieobecności")
                                .withIcon(R.drawable.ic_person_outline_black_48dp),
                        new DividerDrawerItem(),
                        lucky)
                .addStickyDrawerItems(new PrimaryDrawerItem().withIconTintingEnabled(true).withSelectable(false)
                        .withIdentifier(6)
                        .withName("Ustawienia")
                        .withIcon(R.drawable.ic_settings_black_48dp))
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        return selectItem(drawerItem);
                    }
                })
                .withDelayOnDrawerClose(50)
                .withOnDrawerNavigationListener(new Drawer.OnDrawerNavigationListener() {
                    @Override
                    public boolean onNavigationClickListener(View clickedView) {
                        onBackPressed();
                        return true;
                    }
                })
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = drawerBuilder.withToolbar(toolbar).build();

        int i = getIntent().getIntExtra(NotificationService.DEFAULT_POSITION, -1);
        if (i >= 0) {
            Log.d(TAG, "setup: selecting position " + i);
            drawer.setSelection(i);
        } else {
            Log.d(TAG, "setup: selecting default position ");
            drawer.setSelection(0);
        }
        ((MainFragment) currentFragment).setOnSetupCompleteListener(refreshListener);
    }

    private void refresh() {
        Toast.makeText(getApplicationContext(), "Refresh started", Toast.LENGTH_SHORT);
        Log.d(TAG, "MainActivity: Refresh started");
        LibrusDataLoader.update(librusData, this).done(new DoneCallback<LibrusData>() {
            @Override
            public void onDone(LibrusData result) {
                LibrusDataLoader.save(result, getApplicationContext());
                ((MainFragment) currentFragment).refresh(result);
                Toast.makeText(getApplicationContext(), "Refresh done", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "MainActivity: Refresh done");
            }
        });
    }

    private void changeFragment(Fragment fragment, String title) {
        if (currentFragment == null || currentFragment.getClass() != fragment.getClass()) {
            Log.d(TAG, "changeFragment: \n" +
                    "fragment " + fragment + "\n" +
                    "title: " + title);
            currentFragment = fragment;
            toolbar.setTitle(title);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_main, fragment);
            transaction.commit();
        }
    }

    private boolean selectItem(IDrawerItem item) {
        switch ((int) item.getIdentifier()) {
            case 0:
                changeFragment(TimetableFragment.newInstance(librusData), "Plan lekcji");
                break;
            case 1:
                changeFragment(GradesFragment.newInstance(librusData), "Oceny");
                break;
            case 2:
                changeFragment(new PlaceholderFragment(), "Terminarz");
                break;
            case 3:
                changeFragment(AnnouncementsFragment.newInstance(librusData), "Ogłoszenia");
                break;
            case 4:
                changeFragment(new PlaceholderFragment(), "Wiadomości");
                break;
            case 5:
                changeFragment(attendanceFragment, "Nieobecności");
                break;
            case 6:
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
                break;
            case 666:
                String date = luckyNumber.getLuckyNumberDay().toString("EEEE, d MMMM yyyy", new Locale("pl"));
                date = date.substring(0, 1).toUpperCase() + date.substring(1).toLowerCase();
                Toast.makeText(this, date, Toast.LENGTH_SHORT).show();
                break;
            default:
                return true;
        }
        return false;
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

    public void addToolbarView(View v) {
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_main);
        appBarLayout.addView(v, 1);
        toolbarView = v;
    }

    public void removeToolbarView() {
        if (toolbarView != null) appBarLayout.removeView(toolbarView);
        toolbarView = null;
    }
}
