package pl.librus.client.sql;

import android.content.Context;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.requery.Persistable;
import java8.util.concurrent.CompletableFuture;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import pl.librus.client.LibrusUtils;
import pl.librus.client.api.APIClient;
import pl.librus.client.api.ProgressReporter;
import pl.librus.client.datamodel.Announcement;
import pl.librus.client.datamodel.Attendance;
import pl.librus.client.datamodel.AttendanceCategory;
import pl.librus.client.datamodel.Average;
import pl.librus.client.datamodel.Event;
import pl.librus.client.datamodel.EventCategory;
import pl.librus.client.datamodel.Grade;
import pl.librus.client.datamodel.GradeCategory;
import pl.librus.client.datamodel.GradeComment;
import pl.librus.client.datamodel.Identifiable;
import pl.librus.client.datamodel.JsonLesson;
import pl.librus.client.datamodel.Lesson;
import pl.librus.client.datamodel.LessonType;
import pl.librus.client.datamodel.LibrusColor;
import pl.librus.client.datamodel.LuckyNumber;
import pl.librus.client.datamodel.Me;
import pl.librus.client.datamodel.PlainLesson;
import pl.librus.client.datamodel.Subject;
import pl.librus.client.datamodel.Teacher;
import pl.librus.client.timetable.TimetableUtils;
import pl.librus.client.ui.MainApplication;

import static pl.librus.client.sql.EntityChange.Type.ADDED;
import static pl.librus.client.sql.EntityChange.Type.CHANGED;

/**
 * Created by szyme on 31.01.2017.
 * Contains methods to update data from server
 */

public class UpdateHelper {
    private final APIClient client;
    @SuppressWarnings("unchecked")
    private final List<Class<? extends Persistable>> entitiesToUpdate = Lists.newArrayList(
            Announcement.class,
            Subject.class,
            Teacher.class,
            Grade.class,
            GradeCategory.class,
            GradeComment.class,
            PlainLesson.class,
            Event.class,
            EventCategory.class,
            Attendance.class,
            AttendanceCategory.class,
            Average.class,
            LibrusColor.class,
            LuckyNumber.class,
            Me.class
    );

    public UpdateHelper(Context context) {
        this(LibrusUtils.getAPIClient(context));
    }

    public UpdateHelper(APIClient client) {
        this.client = client;
    }

    public CompletableFuture<?> updateAll(ProgressReporter progressReporter) {
        LibrusUtils.log("Starting update...");
        List<CompletableFuture<?>> tasks = Lists.newArrayList();
        final long startTime = System.currentTimeMillis();

        for (Class<? extends Persistable> entityClass : entitiesToUpdate) {
            tasks.add(update(entityClass));
        }
        tasks.add(updateNearestTimetables());
        progressReporter.setTotal(tasks.size());
        return CompletableFuture.allOf(StreamSupport.stream(tasks)
                .map(task -> task.thenRun(progressReporter))
                .toArray(CompletableFuture[]::new))
                .thenAccept(result -> {
                    long currentTimeMillis = System.currentTimeMillis();
                    LibrusUtils.log("Update completed in " + (currentTimeMillis - startTime) + " ms");
                });
    }

    private CompletableFuture<?> updateNearestTimetables() {
        List<LocalDate> weekStarts = TimetableUtils.getNextFullWeekStarts(LocalDate.now());
        List<CompletableFuture<?>> tasks = new ArrayList<>(weekStarts.size());
        for (LocalDate weekStart : weekStarts) {
            tasks.add(updateTimetable(weekStart));
        }
        return CompletableFuture.allOf(Iterables.toArray(tasks, CompletableFuture.class));
    }

    private CompletableFuture<List<Lesson>> updateTimetable(LocalDate weekStart) {
        return client.getTimetable(weekStart).thenApply(timetable -> {
            List<Lesson> result = Lists.newArrayList();

            for (Map.Entry<LocalDate, List<List<JsonLesson>>> e : timetable.entrySet()) {
                LocalDate date = e.getKey();
                for (List<JsonLesson> list : e.getValue()) {
                    if (list.size() > 0) {
                        Lesson l = list.get(0).convert(date);
                        result.add(l);
                        MainApplication.getData()
                                .upsert(l);
                    }
                }
            }
            return result;
        });
    }

    private <T extends Persistable> CompletableFuture<Iterable<T>> update(final Class<T> clazz) {
        return client.getAll(clazz)
                .thenApply(MainApplication.getData()::upsert);
    }

    public CompletableFuture<List<Lesson>> getLessonsForWeek(LocalDate weekStart) {
        List<Lesson> cached = MainApplication.getData()
                .select(Lesson.class)
                .where(LessonType.DATE.gte(weekStart))
                .and(LessonType.DATE.lt(weekStart.plusWeeks(1)))
                .get()
                .toList();

        if (cached.isEmpty()) {
            return updateTimetable(weekStart);
        } else {
            return CompletableFuture.completedFuture(cached);
        }
    }

    public <T extends Persistable & Identifiable> CompletableFuture<List<EntityChange<T>>> reload(Class<T> clazz) {

        List<T> entitiesInDB = MainApplication.getData()
                .select(clazz)
                .get()
                .toList();
        Map<String, T> byId = StreamSupport.stream(entitiesInDB)
                .collect(Collectors.toMap(t -> t.id(), t -> t));
        return client.getAll(clazz)
                .thenApply(newEntities -> {
                    MainApplication.getData().upsert(newEntities);
                    List<EntityChange<T>> changed = Lists.newArrayList();
                    for (T newEntity : newEntities) {
                        T inDB = byId.get(newEntity.id());
                        if (inDB == null) {
                            changed.add(ImmutableEntityChange.of(ADDED, newEntity));
                        } else if (!inDB.equals(newEntity)) {
                            changed.add(ImmutableEntityChange.of(CHANGED, newEntity));
                        }
                    }
                    return changed;
                });
    }

}
