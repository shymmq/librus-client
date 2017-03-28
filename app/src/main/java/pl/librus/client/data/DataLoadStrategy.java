package pl.librus.client.data;


import org.joda.time.LocalDate;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.requery.Persistable;
import pl.librus.client.domain.Identifiable;
import pl.librus.client.domain.lesson.Lesson;

public interface DataLoadStrategy {

    Observable<Lesson> getLessonsForWeek(LocalDate weekStart);

    <T extends Persistable> Observable<T> getAll(Class<T> clazz);

    <T extends Persistable & Identifiable> Single<T> getById(Class<T> clazz, String id);
}
