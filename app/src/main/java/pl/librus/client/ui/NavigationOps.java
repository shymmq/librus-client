package pl.librus.client.ui;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.google.common.base.Optional;

import pl.librus.client.domain.LuckyNumber;
import pl.librus.client.domain.Me;
import pl.librus.client.presentation.FragmentPresenter;

/**
 * Created by robwys on 09/04/2017.
 */

public interface NavigationOps {
    void setupDrawer(Me me, Optional<LuckyNumber> luckyNumber);

    Fragment getCurrentFragmentId();

    @Nullable
    Integer getInitialFragmentTitle();

    void displayFragment(FragmentPresenter fragmentPresenter);

    void setupToolbar();

}
