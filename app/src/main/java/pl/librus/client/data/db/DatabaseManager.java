package pl.librus.client.data.db;


import android.content.Context;

import org.joda.time.LocalDate;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveSupport;
import io.requery.sql.TableCreationMode;
import pl.librus.client.BuildConfig;
import pl.librus.client.UserScope;
import pl.librus.client.data.DataLoadStrategy;
import pl.librus.client.domain.Identifiable;
import pl.librus.client.domain.Models;
import pl.librus.client.domain.lesson.Lesson;
import pl.librus.client.domain.lesson.LessonType;

@UserScope
public class DatabaseManager implements DataLoadStrategy {

    private final ReactiveEntityStore<Persistable> dataStore;
    private final Context context;
    private final String login;

    @Inject
    public DatabaseManager(Context context, @Named("login") String login) {
        this.context = context;
        this.login = login;
        DatabaseSource source = new DatabaseSource(context, Models.DEFAULT, databaseName(login), 18);
        if (BuildConfig.DEBUG) {
            source.setLoggingEnabled(true);
            source.setTableCreationMode(TableCreationMode.DROP_CREATE);
        }
        dataStore = ReactiveSupport.toReactiveStore(SqlHelper.getDataStore(source));
    }

    private String databaseName(String login) {
        return "user-data-" + login;
    }

    public void delete() {
        close();
        context.deleteDatabase(databaseName(login));
    }

    public void close() {
        dataStore.close();
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

    public void upsert(List<? extends Persistable> elements) {
        dataStore.upsert(elements)
            .blockingGet();
    }

    public void clearAll(Class<? extends Persistable> clazz) {
        dataStore.delete(clazz)
                .get()
                .single()
                .blockingGet();
    }

    public ReactiveEntityStore<Persistable> getDataStore() {
        return dataStore;
    }
}
