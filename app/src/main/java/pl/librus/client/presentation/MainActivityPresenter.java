package pl.librus.client.presentation;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.google.common.base.Optional;

import org.immutables.value.Value;

import java.util.Set;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import java8.util.stream.StreamSupport;
import pl.librus.client.MainActivityScope;
import pl.librus.client.MainApplication;
import pl.librus.client.R;
import pl.librus.client.data.LibrusData;
import pl.librus.client.data.Reader;
import pl.librus.client.data.UpdateHelper;
import pl.librus.client.data.db.DatabaseManager;
import pl.librus.client.data.server.HttpException;
import pl.librus.client.data.server.OfflineException;
import pl.librus.client.domain.LuckyNumber;
import pl.librus.client.domain.Me;
import pl.librus.client.domain.grade.Grade;
import pl.librus.client.ui.IntentRunner;
import pl.librus.client.ui.MainActivityOps;
import pl.librus.client.ui.ProgressReporter;
import pl.librus.client.ui.SettingsFragment;
import pl.librus.client.ui.ToastDisplay;
import pl.librus.client.util.LibrusUtils;
import pl.librus.client.util.PreferencesManager;

/**
 * Created by robwys on 28/03/2017.
 */

@MainActivityScope
public class MainActivityPresenter {

    private final DatabaseManager database;
    private final LibrusData data;
    private final MainActivityOps mainActivity;
    private final UpdateHelper updateHelper;
    private final Reader reader;
    private final IntentRunner intentRunner;
    private final ToastDisplay toast;
    private final PreferencesManager preferences;
    private final Set<MainFragmentPresenter> fragmentPresenters;
    private final SettingsPresenter settingsPresenter;

    @Inject
    public MainActivityPresenter(
            DatabaseManager database,
            LibrusData data,
            MainActivityOps mainActivity,
            UpdateHelper updateHelper,
            Reader reader,
            IntentRunner intentRunner,
            ToastDisplay toast,
            PreferencesManager preferences,
            Set<MainFragmentPresenter> fragmentPresenters,
            SettingsPresenter settingsPresenter) {
        this.database = database;
        this.data = data;
        this.mainActivity = mainActivity;
        this.updateHelper = updateHelper;
        this.reader = reader;
        this.intentRunner = intentRunner;
        this.toast = toast;
        this.preferences = preferences;
        this.fragmentPresenters = fragmentPresenters;
        this.settingsPresenter = settingsPresenter;
    }

    public void setup() {
        database
                .getAll(Me.class)
                .singleOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        me -> this.displayInitialData(), //Some data in DB, proceed
                        t -> this.doOneTimeUpdate()); //No data in db. Load everything
    }

    private void doOneTimeUpdate() {
        ProgressReporter reporter = mainActivity.displayProgressDialog();
        updateHelper.updateAll(reporter)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> database.getAll(Grade.class)
                        .subscribe(reader::read))
                .doFinally(mainActivity::hideProgressDialog)
                .subscribe(
                        reporter::report,
                        this::handleUpdateError,
                        this::displayInitialData);
    }

    private void displayInitialData() {
        mainActivity.setupToolbar();

        Single<Optional<LuckyNumber>> singleLuckyNumber = data.findLuckyNumber()
                .map(Optional::of)
                .toSingle()
                .onErrorReturnItem(Optional.absent());
        Single.zip(
                data.findMe(),
                singleLuckyNumber,
                ImmutableDrawerTuple::of
        ).subscribe(drawerTuple -> {
            mainActivity.setupDrawer(drawerTuple.me(), drawerTuple.luckyNumber());
            displayInitialFragment();
        });
    }

    private void handleUpdateError(Throwable exception) {
        LibrusUtils.log("Handle update error");
        if (exception instanceof OfflineException) {
            LibrusUtils.log("Offline mode");
            mainActivity.showSnackBar(R.string.offline_data_error, Snackbar.LENGTH_LONG);
            displayInitialData();

        } else if (exception instanceof HttpException && exception.getMessage().contains("Request is denied")) {
            LibrusUtils.log("Request denied, logout");

            //User probably changed password
            logout();
        } else {
            LibrusUtils.logError("Unknown error");
            exception.printStackTrace();
            mainActivity.showSnackBar(R.string.unknown_error, Snackbar.LENGTH_LONG);
        }
    }

    public void logout() {
        Optional<String> login = preferences.getLogin();
        if (login.isPresent()) {
            preferences.clearAll();

            database.delete();

            toast.display("Wylogowano", Toast.LENGTH_SHORT);
        }
        disableNotifications();

        MainApplication.releaseMainActivityComponent();

        intentRunner.navigateToLogin();

        mainActivity.finish();
    }

    private void disableNotifications() {
        intentRunner.runRegistrationService(false);
    }

    public void luckyNumberClicked(LuckyNumber luckyNumber) {
        if (luckyNumber != null) {
            String luckyDate = luckyNumber.day().toString("EEEE, d MMMM");
            toast.display(luckyDate, Toast.LENGTH_LONG);
        } else {
            toast.display("Brak danych", Toast.LENGTH_LONG);
        }
    }

    private void displayInitialFragment() {
        Fragment currentFragment = mainActivity.getCurrentFragmentId();
        if (currentFragment != null && currentFragment instanceof SettingsFragment) {
            mainActivity.displayFragment(settingsPresenter);
        } else {
            mainActivity.displayFragment(getInitialFragment());
        }
    }

    private FragmentPresenter getInitialFragment() {
        Integer fragmentTitle = mainActivity.getInitialFragmentTitle();

        if (fragmentTitle != null && fragmentTitle > 0) {
            fragmentTitle = preferences.getString("defaultFragment")
                    .transform(Integer::valueOf)
                    .or(-1);
        }
        return getFragmentForTitle(fragmentTitle);
    }

    private MainFragmentPresenter getFragmentForTitle(int fragmentTitle) {
        return StreamSupport.stream(fragmentPresenters)
                .filter(f -> f.getTitle() == fragmentTitle)
                .findFirst()
                .orElse(MainFragmentPresenter.sorted(fragmentPresenters).get(0));
    }

    private MainFragmentPresenter getPresenterForFragmentId(int fragmentId) {
        return StreamSupport.stream(fragmentPresenters)
                .filter(f -> f.getFragment().getId() == fragmentId)
                .findFirst()
                .orElse(MainFragmentPresenter.sorted(fragmentPresenters).get(0));
    }

    public void destroy() {
        database.close();
    }

    @Value.Immutable
    interface DrawerTuple {
        @Value.Parameter
        Me me();

        @Value.Parameter
        Optional<LuckyNumber> luckyNumber();
    }
}
