package pl.librus.client.api;

import org.joda.time.LocalDate;

import java.util.List;

import io.requery.Persistable;
import java8.util.concurrent.CompletableFuture;
import pl.librus.client.datamodel.Timetable;

public interface IAPIClient {
    CompletableFuture<Void> login(String username, String password);

    CompletableFuture<Timetable> getTimetable(LocalDate weekStart);

    <T extends Persistable> CompletableFuture<List<T>> getAll(Class<T> clazz);

    <T> CompletableFuture<List<T>> getList(String endpoint, String topLevelName, Class<T> clazz);

}
