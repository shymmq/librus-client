package pl.librus.client.api;


import android.content.Context;
import android.preference.PreferenceManager;

import com.google.common.base.Preconditions;

import org.joda.time.LocalDate;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveSupport;
import io.requery.sql.TableCreationMode;
import pl.librus.client.BuildConfig;
import pl.librus.client.datamodel.Identifiable;
import pl.librus.client.datamodel.Models;
import pl.librus.client.datamodel.lesson.Lesson;
import pl.librus.client.datamodel.lesson.LessonType;
import pl.librus.client.sql.SqlHelper;

public class DatabaseStrategy implements DataLoadStrategy {

    private static ReactiveEntityStore<Persistable> dataStore;

    private DatabaseStrategy() {

    }

    public static DatabaseStrategy getInstance(Context context, String login) {
        if(dataStore == null) {
            DatabaseSource source = new DatabaseSource(context, Models.DEFAULT, databaseName(login), 16);
            if (BuildConfig.DEBUG) {
                source.setLoggingEnabled(true);
                source.setTableCreationMode(TableCreationMode.DROP_CREATE);
            }
            dataStore = ReactiveSupport.toReactiveStore(SqlHelper.getDataStore(source));
        }
        return new DatabaseStrategy();
    }

    public static DatabaseStrategy getInstance(Context context) {
        String login = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("login", null);
        login = Preconditions.checkNotNull(login);
        return getInstance(context, login);
    }

    private static String databaseName(String login) {
        return "user-data-" + login;
    }

    public static void delete(Context context, String login) {
        close();
        context.deleteDatabase(databaseName(login));
    }

    public static void close() {
        if(dataStore != null) {
            dataStore.close();
            dataStore = null;
        }
    }

    @Override
    public Observable<Lesson> getLessonsForWeek(LocalDate weekStart) {
        return dataStore.select(Lesson.class)
                .where(LessonType.DATE.gte(weekStart))
                .and(LessonType.DATE.lt(weekStart.plusWeeks(1)))
                .get()
                .observable();
    }

    @Override
    public <T extends Persistable> Observable<T> getAll(Class<T> clazz) {
        return dataStore.select(clazz)
                .get()
                .observable();
    }

    @Override
    public <T extends Persistable & Identifiable> Single<T> getById(Class<T> clazz, String id) {
        return dataStore.findByKey(clazz, id)
                .toSingle();
    }

    public <T extends Persistable> void upsert(List<T> elements) {
        dataStore.upsert(elements)
            .blockingGet();
    }

    public ReactiveEntityStore<Persistable> getDataStore() {
        return dataStore;
    }
}
