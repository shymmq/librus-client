package pl.librus.client.presentation;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

import pl.librus.client.ui.MainActivityOps;
import pl.librus.client.ui.View;

/**
 * Created by robwys on 28/03/2017.
 */

public abstract class FragmentPresenter<T extends View> {

    protected T view;

    protected final MainActivityOps mainActivity;

    protected FragmentPresenter(MainActivityOps mainActivity) {
        this.mainActivity = mainActivity;
    }

    public abstract Fragment getFragment();

    @StringRes
    public abstract int getTitle();

    @DrawableRes
    public abstract int getIcon();

    public PrimaryDrawerItem convertToDrawerItem() {
        return new PrimaryDrawerItem()
                .withIconTintingEnabled(true)
                .withSelectable(false)
                .withName(getTitle())
                .withIcon(getIcon())
                .withIdentifier(getTitle())
                .withIconTintingEnabled(true)
                .withOnDrawerItemClickListener((v, p, d) -> drawerItemClicked());
    }

    protected boolean drawerItemClicked() {
        mainActivity.displayFragment(this);
        return false;
    }

    public final void attachView(T view) {
        this.view = view;
        onViewAttached();
    }

    protected void onViewAttached() {

    }
}
