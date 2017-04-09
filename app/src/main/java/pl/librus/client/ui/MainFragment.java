package pl.librus.client.ui;

import android.support.v4.app.Fragment;

import pl.librus.client.presentation.MainFragmentPresenter;

/**
 * Created by szyme on 04.04.2017.
 */

public abstract class MainFragment extends Fragment {

    protected abstract void injectPresenter();

    protected abstract MainFragmentPresenter getPresenter();

    @Override
    public void onStart() {
        injectPresenter();
        super.onStart();
    }

    @Override
    public void onStop() {
        getPresenter().detachView();
        super.onStop();
    }
}
