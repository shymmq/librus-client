package pl.librus.client;

import pl.librus.client.ui.MainActivity;
import pl.librus.client.ui.SettingsFragment;

/**
 * Created by robwys on 28/03/2017.
 */
public interface BaseMainActivityComponent {
    void inject(MainActivity mainActivity);

    void inject(SettingsFragment fragment);
}
