package pl.librus.client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import org.jdeferred.DoneCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final String TAG = "librus-client-log";

    private Toolbar toolbar;
    private Timetable timetable;
    private List<Announcement> announcementList = new ArrayList<Announcement>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean logged_in = prefs.getBoolean("logged_in", false);
        if (!logged_in) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        } else {
            setContentView(R.layout.activity_main);

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            //TODO load and display data from cache here

            APIClient client = new APIClient(this);
            client.getTimetable(TimetableUtils.getWeekStart(), TimetableUtils.getWeekStart().plusWeeks(1)).done(new DoneCallback<Timetable>() {
                @Override
                public void onDone(Timetable result) {
                    Log.d(TAG, "Timetable" + result.getTimetable().toString());
                    timetable = result;
                }
            }).then(new DoneCallback<Timetable>() {
                @Override
                public void onDone(Timetable result) {
                    onNavigationItemSelected(navigationView.getMenu().getItem(0).setChecked(true));
                }
            });

            client.getAnnouncements().done(new DoneCallback<List<Announcement>>() {
                @Override
                public void onDone(List<Announcement> result) {
                    Log.d(TAG, "Announcement count: " + result.size());
                    announcementList = (ArrayList<Announcement>) result;
                }
            }).then(new DoneCallback<List<Announcement>>() {
                @Override
                public void onDone(List<Announcement> result) {
                    onNavigationItemSelected(navigationView.getMenu().getItem(3).setChecked(true));
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        Log.d(TAG, "onNavigationItemSelected: Item: " + item.getTitle());
        switch (item.getItemId()) {
            case R.id.nav_timetable:

                Log.d(TAG, "onNavigationItemSelected: timetable: " + timetable);
                toolbar.setTitle("Plan lekcji");
                setFragment(TimetableFragment.newInstance(timetable));

                break;

            case R.id.nav_grades:

                setFragment(new PlaceholderFragment());
                toolbar.setTitle("Oceny");

                break;

            case R.id.nav_calendar:

                setFragment(new PlaceholderFragment());
                toolbar.setTitle("Terminarz");

                break;

            case R.id.nav_annoucements:
                setFragment(AnnouncementsFragment.newInstance((ArrayList<Announcement>) announcementList));
                toolbar.setTitle("Ogłoszenia");

                break;

            case R.id.nav_messages:

                setFragment(new PlaceholderFragment());
                toolbar.setTitle("Wiadomości");

                break;

            case R.id.nav_attendances:

                setFragment(new PlaceholderFragment());
                toolbar.setTitle("Nieobecności");

                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void setFragment(Fragment fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.content_main, fragment);
        transaction.commit();
    }
}
