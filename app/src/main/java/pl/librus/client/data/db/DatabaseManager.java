package pl.librus.client.data.db;


import android.content.Context;
import android.support.annotation.VisibleForTesting;

import org.joda.time.LocalDate;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveSupport;
import io.requery.sql.TableCreationMode;
import pl.librus.client.BuildConfig;
import pl.librus.client.Models;
import pl.librus.client.UserScope;
import pl.librus.client.data.DataLoadStrategy;
import pl.librus.client.data.LastUpdate;
import pl.librus.client.domain.Identifiable;
import pl.librus.client.domain.lesson.Lesson;
import pl.librus.client.domain.lesson.LessonType;
import pl.librus.client.util.LibrusUtils;

@UserScope
public class DatabaseManager implements DataLoadStrategy {

    private final ReactiveEntityStore<Persistable> dataStore;
    private final Context context;
    private final String login;

    @Inject
    public DatabaseManager(Context context, @Named("login") String login) {
        this.context = context;
        this.login = login;
        DatabaseSource source = new DatabaseSource(context, Models.DEFAULT, databaseName(login), 20);
        source.setTableCreationMode(TableCreationMode.DROP_CREATE);
        if (BuildConfig.DEBUG) {
            source.setLoggingEnabled(true);
        }
        dataStore = ReactiveSupport.toReactiveStore(SqlHelper.getDataStore(source));
    }

    private String databaseName(String login) {
        return "user-data-" + login;
    }

    public void delete() {
        context.deleteDatabase(databaseName(login));
    }

    @Override
    public Observable<Lesson> getLessonsForWeek(LocalDate weekStart) {
        return dataStore.select(Lesson.class)
                .where(LessonType.DATE.gte(weekStart))
                .and(LessonType.DATE.lt(weekStart.plusWeeks(1)))
                .get()
                .observable()
                .subscribeOn(Schedulers.io());
    }

    @Override
    public <T extends Persistable> Observable<T> getAll(Class<T> clazz) {
        return dataStore.select(clazz)
                .get()
                .observable()
                .subscribeOn(Schedulers.io());
    }

    @Override
    public <T extends Persistable & Identifiable> Single<T> getById(Class<T> clazz, String id) {
        return dataStore.findByKey(clazz, id)
                .toSingle()
                .subscribeOn(Schedulers.io());
    }

    public <T extends Persistable> Completable upsert(List<T> elements) {
        return dataStore.upsert(elements)
                .toCompletable()
                .andThen(setLastUpdate(elements))
                .subscribeOn(Schedulers.io());
    }

    private Completable setLastUpdate(List<? extends Persistable> elements) {
        if (elements.isEmpty()) {
            return Completable.complete();
        } else {
            Class<? extends Persistable> clazz = elements.get(0).getClass();
            LastUpdate lastUpdate = LastUpdate.of(clazz, LocalDate.now());
            return dataStore.upsert(lastUpdate)
                    .subscribeOn(Schedulers.io())
                    .toCompletable();
        }

    }

    public Completable clearAll(Class<? extends Persistable> clazz) {
        return dataStore.delete(clazz)
                .get()
                .single()
                .toCompletable();
    }

    public Maybe<LastUpdate> findLastUpdate(Class<? extends Persistable> clazz) {
        return dataStore.findByKey(LastUpdate.class, LibrusUtils.getClassId(clazz))
                .subscribeOn(Schedulers.io());
    }

    @VisibleForTesting
    public ReactiveEntityStore<Persistable> getDataStore() {
        return dataStore;
    }
}
