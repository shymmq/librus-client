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

import com.google.firebase.analytics.FirebaseAnalytics;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import pl.librus.client.R;
import pl.librus.client.grades.GradesFragment;
import pl.librus.client.timetable.TimetableFragment;

public class MainActivity extends AppCompatActivity implements Drawer.OnDrawerItemClickListener {
    public static final int FRAGMENT_TIMETABLE_ID = 0;
    public static final int FRAGMENT_GRADES_ID = 1;
    public static final int FRAGMENT_ANNOUNCEMENTS_ID = 3;
    private static final int FRAGMENT_CALENDAR_ID = 2;
    private final String TAG = "librus-client-log";
    TimetableFragment timetableFragment = TimetableFragment.newInstance();
    private ActionMenuView amv;
    private AppBarLayout appBarLayout;
    private Drawer drawer;
    private Toolbar toolbar;
    private View toolbarView;
    private Fragment fragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseAnalytics.getInstance(getApplicationContext());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean logged_in = prefs.getBoolean("logged_in", false);
        if (!logged_in) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            finish();
        } else {
            setup(savedInstanceState);
        }
    }

    private void setup(Bundle savedInstanceState) {
        //LibrusAccount account = librusData.getAccount();
        //luckyNumber = librusData.getLuckyNumber();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Drawer setup
        ProfileDrawerItem profile = new ProfileDrawerItem()
                .withName("Grzegorz")
                .withEmail("Brzęczyszczykiewicz")
                .withIcon(R.drawable.ic_person_white_48px);
        PrimaryDrawerItem lucky = new PrimaryDrawerItem().withIconTintingEnabled(true).withSelectable(false)
                .withIdentifier(666)
                .withName("Szczęśliwy numerek: " + 99)
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
                .withSavedInstance(savedInstanceState)
                .withSelectedItem(FRAGMENT_TIMETABLE_ID)
                .withFireOnInitialOnClick(true)
                .withToolbar(toolbar);
        drawer = drawerBuilder.build();
    }

    private void refresh() {
        Log.d(TAG, "MainActivity: Refresh started");
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
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (fragment != null) transaction.remove(fragment);

        switch ((int) drawerItem.getIdentifier()) {
            case FRAGMENT_TIMETABLE_ID:
                fragment = timetableFragment;
                break;
            case FRAGMENT_GRADES_ID:
                fragment = GradesFragment.newInstance();
                break;
            default:
                fragment = new PlaceholderFragment();
        }
        fragment.setRetainInstance(true);
        transaction
                .add(R.id.content_main, fragment)
                .commit();

        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (drawer != null) {
            outState = drawer.saveInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
    }
}
