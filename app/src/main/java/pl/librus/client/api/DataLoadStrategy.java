package pl.librus.client.api;


import org.joda.time.LocalDate;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.requery.Persistable;
import pl.librus.client.datamodel.Identifiable;
import pl.librus.client.datamodel.lesson.Lesson;
import pl.librus.client.datamodel.lesson.Timetable;

public interface DataLoadStrategy {

    Observable<Lesson> getLessonsForWeek(LocalDate weekStart);

    <T extends Persistable> Observable<T> getAll(Class<T> clazz);

    <T extends Persistable & Identifiable> Single<T> getById(Class<T> clazz, String id);
}
