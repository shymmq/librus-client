package pl.librus.client.ui;

import android.support.v4.app.Fragment;

import com.google.common.base.Optional;

import pl.librus.client.MainActivityComponent;
import pl.librus.client.MainApplication;
import pl.librus.client.presentation.MainFragmentPresenter;

/**
 * Created by szyme on 04.04.2017.
 */

public abstract class MainFragment extends Fragment {

    protected abstract void injectPresenter(MainActivityComponent mainActivityComponent);

    protected abstract MainFragmentPresenter getPresenter();

    @Override
    public void onStart() {
        Optional<MainActivityComponent> mainActivityComponent =
                MainApplication.getMainActivityComponent();
        if(mainActivityComponent.isPresent()) {
            injectPresenter(mainActivityComponent.get());
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        getPresenter().detachView();
        super.onStop();
    }
}
