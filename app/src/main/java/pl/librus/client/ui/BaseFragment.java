package pl.librus.client.ui;

import android.support.v4.app.Fragment;

import java.util.List;

import java8.util.function.Consumer;

public abstract class BaseFragment extends Fragment implements MainFragment  {

    public void setMenuActionsHandler(Consumer<List<? extends MenuAction>> r) {
        //ignore
    }
}
