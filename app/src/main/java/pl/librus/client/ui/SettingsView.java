package pl.librus.client.ui;

import java.util.List;

import pl.librus.client.presentation.MainFragmentPresenter;

/**
 * Created by szyme on 04.04.2017.
 */

public interface SettingsView extends View {
    void updateAvailableNotifications();

    void updateAvailableFragments(List<? extends MainFragmentPresenter> presenters);
}
