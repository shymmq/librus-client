package pl.librus.client.api;


import org.joda.time.LocalDate;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.requery.Persistable;
import pl.librus.client.datamodel.Identifiable;
import pl.librus.client.datamodel.lesson.Lesson;

public class ServerFallbackStrategy implements DataLoadStrategy {

    private final IAPIClient serverStrategy;

    private final DatabaseStrategy databaseStrategy;

    public ServerFallbackStrategy(IAPIClient serverStrategy, DatabaseStrategy databaseStrategy) {
        this.serverStrategy = serverStrategy;
        this.databaseStrategy = databaseStrategy;
    }

    @Override
    public Observable<Lesson> getLessonsForWeek(LocalDate weekStart) {
        return databaseStrategy.getLessonsForWeek(weekStart)
                .switchIfEmpty(serverStrategy.getLessonsForWeek(weekStart));
    }

    @Override
    public <T extends Persistable> Observable<T> getAll(Class<T> clazz) {
        return databaseStrategy.getAll(clazz)
                .switchIfEmpty(serverStrategy.getAll(clazz));
    }

    @Override
    public <T extends Persistable & Identifiable> Single<T> getById(Class<T> clazz, String id) {
        return databaseStrategy.getById(clazz, id)
                .onErrorResumeNext(serverStrategy.getById(clazz, id));
    }
}
