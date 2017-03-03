package pl.librus.client.api;

import org.joda.time.LocalDate;

import java.util.List;

import io.reactivex.Single;
import io.requery.Persistable;
import pl.librus.client.datamodel.Identifiable;
import pl.librus.client.datamodel.lesson.Timetable;

public interface IAPIClient {
    Single<String> login(String username, String password);

    Single<Timetable> getTimetable(LocalDate weekStart);

    <T extends Persistable> Single<List<T>> getAll(Class<T> clazz);

    <T> Single<List<T>> getList(String endpoint, String topLevelName, Class<T> clazz);

    <T extends Persistable & Identifiable> Single<T> getById(Class<T> clazz, String id);

}
