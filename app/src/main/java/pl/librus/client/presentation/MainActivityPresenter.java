package pl.librus.client.presentation;

import android.widget.Toast;

import com.google.common.base.Optional;

import org.immutables.value.Value;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import pl.librus.client.MainActivityScope;
import pl.librus.client.MainApplication;
import pl.librus.client.data.Reader;
import pl.librus.client.data.UpdateHelper;
import pl.librus.client.data.db.DatabaseManager;
import pl.librus.client.data.server.HttpException;
import pl.librus.client.domain.LuckyNumber;
import pl.librus.client.domain.Me;
import pl.librus.client.domain.announcement.Announcement;
import pl.librus.client.domain.grade.Grade;
import pl.librus.client.ui.MainActivityOps;
import pl.librus.client.ui.ProgressReporter;
import pl.librus.client.ui.ToastDisplay;
import pl.librus.client.util.LibrusUtils;
import pl.librus.client.util.PreferencesManager;
import pl.librus.client.widget.WidgetUpdater;

/**
 * Created by robwys on 28/03/2017.
 */

@MainActivityScope
public class MainActivityPresenter {

    private final DatabaseManager database;
    private final MainActivityOps mainActivity;
    private final UpdateHelper updateHelper;
    private final Reader reader;
    private final PreferencesManager preferences;
    private final ToastDisplay toast;
    private final ErrorHandler errorHandler;
    private final MainNavigationPresenter navigationPresenter;
    private final WidgetUpdater widgetUpdater;

    private boolean activityAttached = true;
    private Disposable subscription;

    @Inject
    public MainActivityPresenter(DatabaseManager database,
                                 MainActivityOps mainActivity,
                                 UpdateHelper updateHelper,
                                 Reader reader,
                                 PreferencesManager preferences,
                                 ToastDisplay toast,
                                 ErrorHandler errorHandler,
                                 MainNavigationPresenter navigationPresenter,
                                 WidgetUpdater widgetUpdater) {
        this.database = database;
        this.mainActivity = mainActivity;
        this.updateHelper = updateHelper;
        this.reader = reader;
        this.preferences = preferences;
        this.toast = toast;
        this.errorHandler = errorHandler;
        this.navigationPresenter = navigationPresenter;
        this.widgetUpdater = widgetUpdater;
    }


    public void setup() {
        subscription = database
                .getAll(Me.class)
                .singleOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        me -> navigationPresenter.setupInitial(), //Some data in DB, proceed
                        t -> this.doOneTimeUpdate()); //No data in db. Load everything
    }

    private void doOneTimeUpdate() {
        ProgressReporter reporter = mainActivity.displayProgressDialog();
        subscription = updateHelper.updateAll(reporter)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> database.getAll(Grade.class)
                        .subscribe(reader::read))
                .doOnComplete(() -> database.getAll(Announcement.class)
                        .subscribe(reader::read))
                .doOnComplete(widgetUpdater::updateLuckyNumber)
                .doFinally(() -> {
                    if (activityAttached) {
                        mainActivity.hideProgressDialog();
                    }
                })
                .subscribe(
                        navigationPresenter::setupInitial,
                        this::handleServerError);
    }

    public void handleServerError(Throwable t) throws Exception {
        if (t instanceof HttpException && t.getMessage().contains("Request is denied")) {
            LibrusUtils.log("Request denied, logout");

            //User probably changed password
            logout();
            return;
        }
        errorHandler.handler(navigationPresenter::setupInitial)
                .accept(t);
    }

    public void luckyNumberClicked(LuckyNumber luckyNumber) {
        if (luckyNumber != null) {
            String luckyDate = luckyNumber.day().toString("EEEE, d MMMM");
            toast.display(luckyDate, Toast.LENGTH_LONG);
        } else {
            toast.display("Brak danych", Toast.LENGTH_LONG);
        }
    }

    public void logout() {
        Optional<String> login = preferences.getLogin();
        if (login.isPresent()) {
            preferences.clearAll();

            database.delete();

            toast.display("Wylogowano", Toast.LENGTH_SHORT);
        }
        mainActivity.unregisterGCM();

        MainApplication.releaseMainActivityComponent();
        MainApplication.releaseUserComponent();

        widgetUpdater.updateLuckyNumber();

        mainActivity.navigateToLogin();

        mainActivity.finish();
    }

    public void destroy() {
        activityAttached = false;
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
    }

    @Value.Immutable
    interface DrawerTuple {
        @Value.Parameter
        Me me();

        @Value.Parameter
        Optional<LuckyNumber> luckyNumber();
    }

}
