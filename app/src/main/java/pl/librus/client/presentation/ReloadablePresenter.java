package pl.librus.client.presentation;

import android.util.Log;

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
import pl.librus.client.util.LibrusUtils;

/**
 * Created by robwys on 07/04/2017.
 */

public abstract class ReloadablePresenter<T extends MainView> extends MainFragmentPresenter<T> {

    protected final UpdateHelper updateHelper;
    protected final ErrorHandler errorHandler;

    protected Disposable subscription;

    protected ReloadablePresenter(UpdateHelper updateHelper, ErrorHandler errorHandler) {
        this.updateHelper = updateHelper;
        this.errorHandler = errorHandler;
    }

    protected abstract Set<Class<? extends Identifiable>> dependentEntities();

    protected abstract Completable refreshView();

    @Override
    protected void onViewAttached() {
        subscription = refreshView()
                .andThen(updateHelper.tryReloadAll())
                .toList()
                .doOnSuccess(changes -> LibrusUtils.log("Reload complete. %s changes", changes.size()))
                .doOnError(t -> LibrusUtils.log("Reload failed"))
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMapCompletable(this::reloadAllComplete)
                .subscribe(() -> {}, errorHandler);
    }

    @Override
    protected void onViewDetached() {
        dispose(subscription);
    }

    private void dispose(Disposable disposable) {
        if(disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    private Completable reloadAllComplete(List<EntityChange> changes) {
        boolean anyRelevantChange = StreamSupport.stream(changes)
                .map(EntityChange::entity)
                .filter(ent -> dependentEntities().contains(ent.getClass()))
                .findAny()
                .isPresent();
        if(anyRelevantChange) {
            return refreshView();
        } else {
            return Completable.complete();
        }
    }

    protected Observable<? extends EntityChange<? extends Identifiable>> reloadRelevantEntities() {
        return updateHelper.reloadMany(dependentEntities());
    }

    public void reload() {
        view.setRefreshing(true);
        subscription = reloadRelevantEntities()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    if(view != null) view.setRefreshing(false);
                })
                .isEmpty()
                .filter(empty -> !empty)
                .flatMapCompletable(e -> refreshView())
                .subscribe(() -> {}, errorHandler);
    }
}
