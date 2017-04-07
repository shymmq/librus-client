package pl.librus.client.presentation;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.librus.client.data.UpdateHelper;
import pl.librus.client.domain.Identifiable;
import pl.librus.client.ui.MainActivityOps;
import pl.librus.client.ui.MainView;

/**
 * Created by robwys on 07/04/2017.
 */

public abstract class ReloadablePresenter<T extends MainView> extends MainFragmentPresenter<T> {

    protected final UpdateHelper updateHelper;

    protected ReloadablePresenter(MainActivityOps mainActivity, UpdateHelper updateHelper) {
        super(mainActivity);
        this.updateHelper = updateHelper;
    }

    protected abstract List<Class<? extends Identifiable>> dependentEntities();

    protected abstract void refreshView();

    @Override
    protected void onViewAttached() {
        refreshView();
    }

    public void reload() {
        view.setRefreshing(true);
        updateHelper.reloadMany(dependentEntities())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> view.setRefreshing(false))
                .isEmpty()
                .subscribe(empty -> {
                    if (!empty) {
                        refreshView();
                    }
                });
    }
}
