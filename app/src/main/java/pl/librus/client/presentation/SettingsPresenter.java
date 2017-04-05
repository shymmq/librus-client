package pl.librus.client.presentation;

import android.support.v4.app.Fragment;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import pl.librus.client.MainActivityScope;
import pl.librus.client.R;
import pl.librus.client.ui.MainActivityOps;
import pl.librus.client.ui.SettingsFragment;
import pl.librus.client.ui.SettingsView;

/**
 * Created by robwys on 28/03/2017.
 */

@MainActivityScope
public class SettingsPresenter extends FragmentPresenter<SettingsView> {

    private final Set<MainFragmentPresenter> fragmentPresenters;

    @Inject
    public SettingsPresenter(MainActivityOps mainActivity, Set<MainFragmentPresenter> fragmentPresenters) {
        super(mainActivity);
        this.fragmentPresenters = fragmentPresenters;
    }

    @Override
    public Fragment getFragment() {
        return new SettingsFragment();
    }

    @Override
    public int getTitle() {
        return R.string.settings_title;
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_settings_black_48dp;
    }

    @Override
    protected void onViewAttached() {
        view.updateAvailableFragments(getFragmentPresenters());
        view.updateAvailableNotifications();
    }

    private List<MainFragmentPresenter> getFragmentPresenters() {
        return MainFragmentPresenter.sorted(fragmentPresenters);
    }
}
