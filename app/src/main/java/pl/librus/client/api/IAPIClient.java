package pl.librus.client.api;

import org.joda.time.LocalDate;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.requery.Persistable;
import pl.librus.client.datamodel.Identifiable;
import pl.librus.client.datamodel.lesson.Timetable;

public interface IAPIClient extends DataLoadStrategy {

    Single<String> login(String username, String password);

    <T> Observable<T> getAll(String endpoint, String topLevelName, Class<T> clazz);
}
