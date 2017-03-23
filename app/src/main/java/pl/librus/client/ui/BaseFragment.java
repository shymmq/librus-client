package pl.librus.client.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import java8.util.function.Consumer;

/**
 * Created by robwys on 22/03/2017.
 */

public abstract class BaseFragment extends Fragment implements MainFragment  {

    public void setMenuActionsHandler(Consumer<List<? extends MenuAction>> r) {
        //ignore
    }
}
