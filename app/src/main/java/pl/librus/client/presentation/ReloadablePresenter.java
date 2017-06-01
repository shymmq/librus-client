package pl.librus.client.presentation;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java8.util.stream.StreamSupport;
import pl.librus.client.data.EntityChange;
import pl.librus.client.data.UpdateHelper;
import pl.librus.client.domain.Identifiable;
import pl.librus.client.ui.MainActivityOps;
import pl.librus.client.ui.MainView;
import pl.librus.client.ui.MenuAction;
import pl.librus.client.ui.ReloadAction;
import pl.librus.client.util.LibrusUtils;

/**
 * Created by robwys on 07/04/2017.
 */

public abstract class ReloadablePresenter<Q, T extends MainView<Q>> extends MainFragmentPresenter<T> {

    protected final MainActivityOps mainActivity;
    protected final UpdateHelper updateHelper;
    protected final ErrorHandler errorHandler;

    protected Disposable subscription;

    private boolean reloading = false;

    protected ReloadablePresenter(MainActivityOps mainActivity, UpdateHelper updateHelper, ErrorHandler errorHandler) {
        this.mainActivity = mainActivity;
        this.updateHelper = updateHelper;
        this.errorHandler = errorHandler;
    }

    protected abstract Set<Class<? extends Identifiable>> dependentEntities();

    protected abstract Single<Q> fetchData();

    @Override
    protected void onViewAttached() {
        subscription = refreshView()
                .andThen(updateHelper.tryReloadAll())
                .toList()
                .doOnSuccess(changes -> LibrusUtils.log("Reload complete. %s changes", changes.size()))
                .doOnError(t -> LibrusUtils.log("Reload failed"))
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMapCompletable(this::reloadAllComplete)
                .subscribe(() -> {
                }, errorHandler);
    }

    public void refresh() {
        subscription = refreshView()
                .subscribe(() -> {
                }, errorHandler);
    }

    @Override
    protected void onViewDetached() {
        dispose(subscription);
        mainActivity.displayMenuActions(Collections.emptyList());
    }

    private void dispose(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    protected Completable refreshView() {
        return fetchData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(this::displayMenuActions)
                .flatMapCompletable(data -> Completable.fromAction(() -> displayData(data)));
    }

    protected void displayData(Q data) {
        view.display(data);
    }

    protected List<MenuAction> getMenuActions(Q data) {
        return Lists.newArrayList(new ReloadAction(this));
    }

    private void displayMenuActions(Q data) {
        mainActivity.displayMenuActions(getMenuActions(data));
    }

    private Completable reloadAllComplete(List<EntityChange> changes) {
        boolean anyRelevantChange = StreamSupport.stream(changes)
                .map(EntityChange::entity)
                .filter(ent -> dependentEntities().contains(ent.getClass()))
                .findAny()
                .isPresent();
        if (anyRelevantChange) {
            return refreshView();
        } else {
            return Completable.complete();
        }
    }

    protected Observable<? extends EntityChange<? extends Identifiable>> reloadRelevantEntities() {
        return updateHelper.reloadMany(dependentEntities());
    }

    public void reload() {
        if (reloading || view == null) {
            return;
        }
        reloading = true;
        mainActivity.updateMenu();
        view.setRefreshing(true);
        subscription = reloadRelevantEntities()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    if (view != null) view.setRefreshing(false);
                    reloading = false;
                    mainActivity.updateMenu();
                })
                .isEmpty()
                .filter(empty -> !empty)
                .flatMapCompletable(e -> refreshView())
                .subscribe(() -> {
                }, errorHandler);
    }

    public boolean isReloading() {
        return reloading;
    }
}
