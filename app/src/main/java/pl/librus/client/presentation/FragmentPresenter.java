package pl.librus.client.presentation;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

import io.reactivex.functions.Action;
import io.reactivex.functions.BiConsumer;
import pl.librus.client.ui.View;

/**
 * Created by robwys on 28/03/2017.
 */

public abstract class FragmentPresenter<T extends View> {

    protected T view;

    public abstract Fragment getFragment();

    @StringRes
    public abstract int getTitle();

    @DrawableRes
    public abstract int getIcon();

    public PrimaryDrawerItem convertToDrawerItem(java8.util.function.Consumer<FragmentPresenter> displayFragment) {
        return new PrimaryDrawerItem()
                .withIconTintingEnabled(true)
                .withSelectable(false)
                .withName(getTitle())
                .withIcon(getIcon())
                .withIdentifier(getTitle())
                .withIconTintingEnabled(true)
                .withOnDrawerItemClickListener((v, p, d) -> {
                    displayFragment.accept(this);
                    return false;
                });
    }

    public final void attachView(T view) {
        this.view = view;
        onViewAttached();
    }

    protected void onViewAttached() {

    }

    public final void detachView() {
        this.view = null;
        onViewDetached();
    }

    protected void onViewDetached() {

    }

    protected <H> io.reactivex.functions.Consumer<H> ifViewAttached(BiConsumer<T, H> consumer) {
        return value -> {
            if (view != null) {
                consumer.accept(view, value);
            }
        };
    }

    protected Action ifViewAttached(io.reactivex.functions.Consumer<T> consumer) {
        return () -> {
            if (view != null) {
                consumer.accept(view);
            }
        };
    }
}
