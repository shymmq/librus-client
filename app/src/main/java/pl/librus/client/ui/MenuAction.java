package pl.librus.client.ui;

import android.support.annotation.StringRes;

/**
 * Created by szyme on 05.02.2017.
 */

public interface MenuAction {
    @StringRes
    int getName();

    void run();

    boolean isEnabled();
}
