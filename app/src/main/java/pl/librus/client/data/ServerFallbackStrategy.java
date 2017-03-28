package pl.librus.client.data;


import org.joda.time.LocalDate;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.requery.Persistable;
import pl.librus.client.UserScope;
import pl.librus.client.data.db.DatabaseManager;
import pl.librus.client.data.server.APIClient;
import pl.librus.client.data.server.IAPIClient;
import pl.librus.client.domain.Identifiable;
import pl.librus.client.domain.lesson.Lesson;

@UserScope
public class ServerFallbackStrategy implements DataLoadStrategy {

    private final IAPIClient serverStrategy;

    private final DatabaseManager databaseStrategy;

    @Inject
    public ServerFallbackStrategy(IAPIClient serverStrategy, DatabaseManager databaseStrategy) {
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
