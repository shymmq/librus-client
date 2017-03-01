package pl.librus.client.api;

import org.joda.time.LocalDate;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.requery.Persistable;
import java8.util.concurrent.CompletableFuture;
import pl.librus.client.datamodel.Timetable;

public interface IAPIClient {
    Single<String> login(String username, String password);

    Single<Timetable> getTimetable(LocalDate weekStart);

    <T extends Persistable> Single<List<T>> getAll(Class<T> clazz);

    <T> Single<List<T>> getList(String endpoint, String topLevelName, Class<T> clazz);

}
