package pl.librus.client.data;


import org.joda.time.LocalDate;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.requery.Persistable;
import pl.librus.client.data.db.DatabaseManager;
import pl.librus.client.data.server.HttpException;
import pl.librus.client.data.server.IAPIClient;
import pl.librus.client.domain.Identifiable;
import pl.librus.client.domain.lesson.Lesson;

public class DatabaseFallbackStrategy implements DataLoadStrategy {

    private final IAPIClient serverStrategy;

    private final DatabaseManager databaseStrategy;

    public DatabaseFallbackStrategy(IAPIClient serverStrategy, DatabaseManager databaseStrategy) {
        this.serverStrategy = serverStrategy;
        this.databaseStrategy = databaseStrategy;
    }

    @Override
    public Observable<Lesson> getLessonsForWeek(LocalDate weekStart) {
        return serverStrategy.getLessonsForWeek(weekStart)
                .onErrorResumeNext(ifOffline(databaseStrategy.getLessonsForWeek(weekStart)));
    }

    @Override
    public <T extends Persistable> Observable<T> getAll(Class<T> clazz) {
        return serverStrategy.getAll(clazz)
                .onErrorResumeNext(ifOffline(databaseStrategy.getAll(clazz)));
    }

    @Override
    public <T extends Persistable & Identifiable> Single<T> getById(Class<T> clazz, String id) {
        return databaseStrategy.getById(clazz, id)
                .onErrorResumeNext(ifOffline(serverStrategy.getById(clazz, id)));
    }

    private <T> Function<Throwable, Single<T>> ifOffline(Single<T> fallback) {
        return throwable -> {
            if (throwable instanceof HttpException) {
                return fallback;
            } else {
                return Single.error(throwable);
            }
        };
    }

    private <T> Function<Throwable, Observable<T>> ifOffline(Observable<T> fallback) {
        return throwable -> {
            if (throwable instanceof HttpException) {
                return fallback;
            } else {
                return Observable.error(throwable);
            }
        };
    }
}
