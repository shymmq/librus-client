package pl.librus.client.api;

import org.joda.time.LocalDate;

import java.util.List;

import io.requery.Persistable;
import java8.util.concurrent.CompletableFuture;
import pl.librus.client.datamodel.Timetable;

/**
 * Created by szyme on 14.02.2017.
 */
public interface APIClient {

    CompletableFuture<Void> login(String username, String password);

    CompletableFuture<Timetable> getTimetable(LocalDate weekStart);

    <T extends Persistable> CompletableFuture<List<T>> getAll(Class<T> clazz);

    <T> CompletableFuture<T> getObject(String endpoint, String topLevelName, Class<T> clazz);

    <T> CompletableFuture<List<T>> getList(String endpoint, String topLevelName, Class<T> clazz);
}
