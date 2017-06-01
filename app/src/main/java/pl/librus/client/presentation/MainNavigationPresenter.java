package pl.librus.client.presentation;

import android.support.v4.app.Fragment;

import java.util.Set;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import java8.util.stream.StreamSupport;
import pl.librus.client.data.LibrusData;
import pl.librus.client.ui.NavigationOps;
import pl.librus.client.ui.SettingsFragment;
import pl.librus.client.util.PreferencesManager;

/**
 * Created by robwys on 09/04/2017.
 */

public class MainNavigationPresenter {

    private final NavigationOps navigation;

    private final LibrusData data;

    private final Set<MainFragmentPresenter> fragmentPresenters;

    private final SettingsPresenter settingsPresenter;

    private final PreferencesManager preferences;

    private final ErrorHandler errorHandler;

    @Inject
    public MainNavigationPresenter(NavigationOps navigation,
                                   LibrusData data,
                                   Set<MainFragmentPresenter> fragmentPresenters,
                                   SettingsPresenter settingsPresenter,
                                   PreferencesManager preferences,
                                   ErrorHandler errorHandler) {
        this.navigation = navigation;
        this.data = data;
        this.fragmentPresenters = fragmentPresenters;
        this.settingsPresenter = settingsPresenter;
        this.preferences = preferences;
        this.errorHandler = errorHandler;
    }


    public void setupInitial() {
        navigation.setupToolbar();


        Single.zip(
                data.findMe(),
                data.findLuckyNumber(),
                ImmutableDrawerTuple::of)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(drawerTuple -> {
                    navigation.setupDrawer(drawerTuple.me(), drawerTuple.luckyNumber());
                    displayInitialFragment();
                }, errorHandler);
    }

    private void displayInitialFragment() {
        Fragment currentFragment = navigation.getCurrentFragmentId();
        if (currentFragment != null && currentFragment instanceof SettingsFragment) {
            navigation.displayFragment(settingsPresenter);
        } else {
            navigation.displayFragment(getInitialFragment());
        }
    }

    private FragmentPresenter getInitialFragment() {
        Integer fragmentFromNotification = navigation.getInitialFragmentTitle();

        if (fragmentFromNotification != null && fragmentFromNotification > 0) {
            return getFragmentForTitle(fragmentFromNotification);
        } else {
            Integer defaultFragment = preferences.getString("defaultFragment")
                    .transform(Integer::valueOf)
                    .or(-1);
            return getFragmentForTitle(defaultFragment);
        }

    }

    private MainFragmentPresenter getFragmentForTitle(int fragmentTitle) {
        return StreamSupport.stream(fragmentPresenters)
                .filter(f -> f.getTitle() == fragmentTitle)
                .findFirst()
                .orElse(MainFragmentPresenter.sorted(fragmentPresenters).get(0));
    }

    public void displayFragment(FragmentPresenter fragmentPresenter) {
        navigation.displayFragment(fragmentPresenter);
    }


}
