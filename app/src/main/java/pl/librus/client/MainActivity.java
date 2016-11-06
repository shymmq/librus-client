package pl.librus.client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDoneCallback;
import org.jdeferred.android.AndroidExecutionScope;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "librus-client-log";
    TimetableFragment timetableFragment;
    AnnouncementsFragment announcementsFragment;
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
            LibrusCache.load(this).done(new AndroidDoneCallback<LibrusCache>() {
                @Override
                public AndroidExecutionScope getExecutionScope() {
                    return null;
                }

                @Override
                public void onDone(LibrusCache result) {
                    display(result);
                }
            }).fail(new FailCallback<Promise<LibrusCache, Object, Object>>() {
                @Override
                public void onFail(Promise<LibrusCache, Object, Object> result) {
                    result.done(new AndroidDoneCallback<LibrusCache>() {
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
            });
        }
    }

    private void display(LibrusCache result) {
        timetableFragment = TimetableFragment.newInstance(result.getTimetable());
        announcementsFragment = AnnouncementsFragment.newInstance(result.getAnnouncements());
        LibrusAccount account = result.getAccount();
        ProfileDrawerItem profile = new ProfileDrawerItem().withName(account.getName()).withEmail(account.getEmail()).withIcon(R.drawable.jeb);

        AccountHeader header = new AccountHeaderBuilder()
                .withActivity(this)
                .withSelectionListEnabledForSingleProfile(true)
                .withHeaderBackground(R.drawable.background_nav)
                .withHeaderBackgroundScaleType(ImageView.ScaleType.CENTER_CROP)
                .addProfiles(profile)
                .build();

        final DrawerBuilder drawer = new DrawerBuilder()
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
                                .withIcon(R.drawable.ic_person_outline_black_48dp))
                .addStickyDrawerItems(new PrimaryDrawerItem().withIconTintingEnabled(true)
                        .withIdentifier(6)
                        .withName("Ustawienia")
                        .withIcon(R.drawable.ic_settings_black_48dp))
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        return selectItem(drawerItem);
                    }
                })
                .withDelayOnDrawerClose(50);

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer.withToolbar(toolbar)
                .build()
                .setSelection(0);

    }

    boolean selectItem(IDrawerItem item) {
        Fragment fragment = null;
        toolbar.setTitle("");
        switch ((int) item.getIdentifier()) {
            case 0:
                fragment = timetableFragment;
                toolbar.setTitle("Plan lekcji");
                break;
            case 1:
                fragment = new PlaceholderFragment();
                break;
            case 2:
                fragment = new PlaceholderFragment();
                break;
            case 3:
                fragment = announcementsFragment;
                toolbar.setTitle("Ogłoszenia");
                break;
            case 4:
                fragment = new PlaceholderFragment();
                break;
            case 5:
                fragment = new PlaceholderFragment();
                break;
            case 6:
                fragment = new PlaceholderFragment();
                break;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (fragment instanceof TimetableFragment) {
                toolbar.setElevation(0);
            } else {
                toolbar.setElevation(4);
            }
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
//        Log.d(TAG, "updateFragment: \n" +
//                "fragment " + fragment + "\n" +
//                "transaction " + transaction);
        transaction.replace(R.id.content_main, fragment);
        transaction.commit();
        return false;
    }
}
