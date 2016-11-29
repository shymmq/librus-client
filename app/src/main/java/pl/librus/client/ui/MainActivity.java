package pl.librus.client.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

import java.util.Locale;

import pl.librus.client.R;
import pl.librus.client.announcements.AnnouncementsFragment;
import pl.librus.client.api.LibrusAccount;
import pl.librus.client.api.LibrusCache;
import pl.librus.client.api.LuckyNumber;
import pl.librus.client.timetable.TimetableFragment;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "librus-client-log";
    LuckyNumber luckyNumber;
    ActionMenuView amv;
    AppBarLayout appBarLayout;
    private TimetableFragment timetableFragment;
    private AnnouncementsFragment announcementsFragment;
    private Drawer drawer;
    private Toolbar toolbar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean logged_in = prefs.getBoolean("logged_in", false);
        if (!logged_in) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        } else {
            LibrusCache.update(this).done(new AndroidDoneCallback<LibrusCache>() {
                @Override
                public AndroidExecutionScope getExecutionScope() {
                    return null;
                }

                @Override
                public void onDone(LibrusCache result) {
                    display(result);
                }
            });
        }
    }

    private void display(LibrusCache result) {
        timetableFragment = TimetableFragment.newInstance(result.getTimetable());
        announcementsFragment = AnnouncementsFragment.newInstance(result.getAnnouncements());
        LibrusAccount account = result.getAccount();
        luckyNumber = result.getLuckyNumber();

        ProfileDrawerItem profile = new ProfileDrawerItem().withName(account.getName()).withEmail(account.getEmail()).withIcon(R.drawable.jeb);

        AccountHeader header = new AccountHeaderBuilder()
                .withActivity(this)
                .withSelectionListEnabledForSingleProfile(true)
                .withHeaderBackground(R.drawable.background_nav)
                .withHeaderBackgroundScaleType(ImageView.ScaleType.CENTER_CROP)
                .addProfiles(profile)
                .build();
        PrimaryDrawerItem lucky = new PrimaryDrawerItem().withIconTintingEnabled(true).withSelectable(false)
                .withIdentifier(666)
                .withName("Szczęśliwy numerek: " + luckyNumber.getLuckyNumber())
                .withIcon(R.drawable.ic_sentiment_very_satisfied_black_24dp);
        final DrawerBuilder drawerBuilder = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIconTintingEnabled(true)
                                .withIdentifier(0)
                                .withName("Plan lekcji")
                                .withIcon(R.drawable.ic_event_note_black_48dp),
                        new PrimaryDrawerItem().withIconTintingEnabled(true)
                                .withIdentifier(1)
                                .withName("Oceny")
                                .withIcon(R.drawable.ic_event_black_48dp),
                        new PrimaryDrawerItem().withIconTintingEnabled(true)
                                .withIdentifier(2)
                                .withName("Terminarz")
                                .withIcon(R.drawable.ic_date_range_black_48dp),
                        new PrimaryDrawerItem().withIconTintingEnabled(true)
                                .withIdentifier(3)
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

        setContentView(R.layout.activity_main);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = drawerBuilder.withToolbar(toolbar)
                .build();
        drawer.setSelection(3);

    }

    void changeFragment(Fragment fragment, String title) {

        Log.d(TAG, "changeFragment: \n" +
                "fragment " + fragment + "\n" +
                "title: " + title);

        toolbar.setTitle(title);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_main, fragment);
        transaction.commit();
    }

    private boolean selectItem(IDrawerItem item) {

        switch ((int) item.getIdentifier()) {
            case 0:
                changeFragment(timetableFragment, "Plan lekcji");
                break;
            case 1:
                changeFragment(new PlaceholderFragment(), "Oceny");
                break;
            case 2:
                changeFragment(new PlaceholderFragment(), "Terminarz");
                break;
            case 3:
                changeFragment(announcementsFragment, "Ogłoszenia");
                break;
            case 4:
                changeFragment(new PlaceholderFragment(), "Wiadomości");
                break;
            case 5:
                changeFragment(new PlaceholderFragment(), "Nieobecności");
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
        }
        return false;
    }

    public Drawer getDrawer() {
        return drawer;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public AppBarLayout getAppBarLayout() {
        return appBarLayout;
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
                LibrusCache.update(getApplicationContext()).done(new DoneCallback<LibrusCache>() {
                    @Override
                    public void onDone(LibrusCache result) {
                        display(result);
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
