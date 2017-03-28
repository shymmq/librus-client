package pl.librus.client.presentation;

import android.support.v4.app.Fragment;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import pl.librus.client.MainActivityScope;
import pl.librus.client.R;
import pl.librus.client.ui.MainActivityOps;
import pl.librus.client.ui.SettingsFragment;

/**
 * Created by robwys on 28/03/2017.
 */

@MainActivityScope
public class SettingsPresenter extends FragmentPresenter {

    private final Set<MainFragmentPresenter> fragmentPresenters;
    private SettingsFragment fragment;

    @Inject
    public SettingsPresenter(MainActivityOps mainActivity, Set<MainFragmentPresenter> fragmentPresenters) {
        super(mainActivity);
        this.fragmentPresenters = fragmentPresenters;
        this.fragment = new SettingsFragment();
    }

    @Override
    public Fragment getFragment() {
        return fragment;
    }

    @Override
    public int getTitle() {
        return R.string.settings_title;
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_settings_black_48dp;
    }

    public List<MainFragmentPresenter> getFragmentPresenters() {
        return MainFragmentPresenter.sorted(fragmentPresenters);
    }

    public void setFragment(SettingsFragment fragment) {
        this.fragment = fragment;
    }
}
