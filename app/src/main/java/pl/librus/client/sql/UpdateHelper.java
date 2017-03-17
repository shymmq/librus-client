package pl.librus.client.sql;

import android.content.Context;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.requery.Persistable;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import pl.librus.client.api.LibrusData;
import pl.librus.client.api.ProgressReporter;
import pl.librus.client.datamodel.announcement.Announcement;
import pl.librus.client.datamodel.attendance.Attendance;
import pl.librus.client.datamodel.attendance.AttendanceCategory;
import pl.librus.client.datamodel.Average;
import pl.librus.client.datamodel.Event;
import pl.librus.client.datamodel.EventCategory;
import pl.librus.client.datamodel.grade.Grade;
import pl.librus.client.datamodel.grade.GradeCategory;
import pl.librus.client.datamodel.grade.GradeComment;
import pl.librus.client.datamodel.Identifiable;
import pl.librus.client.datamodel.LibrusColor;
import pl.librus.client.datamodel.LuckyNumber;
import pl.librus.client.datamodel.Me;
import pl.librus.client.datamodel.PlainLesson;
import pl.librus.client.datamodel.subject.Subject;
import pl.librus.client.datamodel.Teacher;

import static pl.librus.client.sql.EntityChange.Type.ADDED;
import static pl.librus.client.sql.EntityChange.Type.CHANGED;

/**
 * Created by szyme on 31.01.2017.
 * Contains methods to update data from server
 */

public class UpdateHelper {
    @SuppressWarnings("unchecked")
    private static final List<Class<? extends Persistable>> entitiesToUpdate = Lists.newArrayList(
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
    private final LibrusData librusData;

    public UpdateHelper(LibrusData librusData) {
        this.librusData = librusData;
    }

    public Flowable<Object> updateAll(ProgressReporter progressReporter) {
        ImmutableList.Builder<Single<?>> builder = ImmutableList.builder();

        for (Class<? extends Persistable> entityClass : entitiesToUpdate) {
            builder.add(librusData
                    .updateAllFromServer(entityClass));
        }
        builder.addAll(updateNearestTimetables());
        List<Single<?>> tasks = builder.build();
        progressReporter.setTotal(tasks.size());
        return Single.merge(tasks);
    }

    private List<Single<?>> updateNearestTimetables() {
        LocalDate lastMonday = LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY);
        List<LocalDate> weekStarts = Lists.newArrayList(lastMonday, lastMonday.plusWeeks(1));
        return StreamSupport.stream(weekStarts)
                .map(librusData::updateTimetableFromServer)
                .collect(Collectors.toList());
    }

    public <T extends Persistable & Identifiable, E> Single<List<EntityChange<T>>> reload(Class<T> clazz) {
        return Single.concat(
                librusData.findAllInDb(clazz),
                librusData.updateAllFromServer(clazz))
                .toList(2)
                .map(this::detectChanges);
    }

    private <T extends Persistable & Identifiable> List<EntityChange<T>> detectChanges(List<List<T>> result) {
        Preconditions.checkArgument(result.size() == 2);
        List<T> fromDb = result.get(0);
        List<T> fromServer = result.get(1);
        Map<String, T> byId = Maps.uniqueIndex(fromDb, t -> t.id());

        ImmutableList.Builder builder = ImmutableList.<EntityChange<T>>builder();
        for (T newEntity : fromServer) {
            T inDB = byId.get(newEntity.id());
            if (inDB == null) {
                builder.add(ImmutableEntityChange.of(ADDED, newEntity));
            } else if (!inDB.equals(newEntity)) {
                builder.add(ImmutableEntityChange.of(CHANGED, newEntity));
            }
        }
        return builder.build();
    }

}
