package pl.librus.client.ui;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import java8.util.function.Consumer;

/**
 * Created by szyme on 05.12.2016.
 * Interface for all directly shown fragments
 */

public interface MainFragment {

    void setMenuActionsHandler(Consumer<List<? extends MenuAction>> handler);

    @StringRes
    int getTitle();

    @DrawableRes
    int getIcon();

}
