package pl.librus.client.api;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;

import java8.util.stream.IntStreams;
import java8.util.stream.StreamSupport;
import pl.librus.client.datamodel.Average;
import pl.librus.client.datamodel.Event;
import pl.librus.client.datamodel.EventCategory;
import pl.librus.client.datamodel.Identifiable;
import pl.librus.client.datamodel.ImmutableAverage;
import pl.librus.client.datamodel.ImmutableEvent;
import pl.librus.client.datamodel.ImmutablePlainLesson;
import pl.librus.client.datamodel.ImmutableTeacher;
import pl.librus.client.datamodel.LibrusColor;
import pl.librus.client.datamodel.LuckyNumber;
import pl.librus.client.datamodel.Me;
import pl.librus.client.datamodel.PlainLesson;
import pl.librus.client.datamodel.Teacher;
import pl.librus.client.datamodel.announcement.Announcement;
import pl.librus.client.datamodel.announcement.ImmutableAnnouncement;
import pl.librus.client.datamodel.attendance.Attendance;
import pl.librus.client.datamodel.attendance.AttendanceCategory;
import pl.librus.client.datamodel.attendance.ImmutableAttendance;
import pl.librus.client.datamodel.grade.Grade;
import pl.librus.client.datamodel.grade.GradeCategory;
import pl.librus.client.datamodel.grade.GradeComment;
import pl.librus.client.datamodel.grade.ImmutableGrade;
import pl.librus.client.datamodel.grade.ImmutableGradeCategory;
import pl.librus.client.datamodel.subject.ImmutableSubject;
import pl.librus.client.datamodel.subject.Subject;
import pl.librus.client.sql.EntityUpdate;
import pl.librus.client.sql.ImmutableEntityUpdate;

import static java8.util.stream.Collectors.toList;
import static pl.librus.client.api.SampleValues.COLORS;
import static pl.librus.client.api.SampleValues.SUBJECTS;

/**
 * Created by szyme on 16.02.2017.
 */

class MockEntityRepository {

    private static final Map<Class<?>, List<?>> lists = Maps.newHashMap();
    private final EntityMocks templates = new EntityMocks();
    private final Random random = new Random(42);

    private final ImmutableMultimap<Class<?>, Class<?>> entitiesRelations = ImmutableMultimap.<Class<?>, Class<?>>builder()
            .putAll(PlainLesson.class, Teacher.class, Subject.class)
            .putAll(Average.class, Subject.class)
            .putAll(Announcement.class, Teacher.class)
            .putAll(Attendance.class, AttendanceCategory.class, PlainLesson.class)
            .putAll(AttendanceCategory.class, LibrusColor.class)
            .putAll(Event.class, EventCategory.class)
            .putAll(Grade.class, GradeCategory.class, GradeComment.class, PlainLesson.class)
            .putAll(GradeCategory.class, LibrusColor.class)
            .build();

    List<EntityUpdate<?>> updates = ImmutableList.<EntityUpdate<?>>builder()
            .add(makeUpdate(Subject.class, SUBJECTS.size(), this::updateSubject))
            .add(makeUpdate(Average.class, SUBJECTS.size(), this::updateAverage))
            .add(makeUpdate(Teacher.class, SUBJECTS.size() - 1, this::updateTeacher))
            .add(makeUpdate(PlainLesson.class, SUBJECTS.size(), this::updatePlainLesson))
            .add(makeUpdate(Announcement.class, 15, this::updateAnnouncement))
            .add(makeUpdate(AttendanceCategory.class, 3, this::updateAttendanceCategory))
            .add(makeUpdate(Attendance.class, 600, this::updateAttendance))
            .add(makeUpdate(EventCategory.class, 3, this::updateEventCategory))
            .add(makeUpdate(Event.class, 15, this::updateEvent))
            .add(makeUpdate(LibrusColor.class, COLORS.size(), this::updateColor))
            .add(makeUpdate(GradeComment.class, 15, this::updateGradeComment))
            .add(makeUpdate(GradeCategory.class, 5, this::updateGradeCategory))
            .add(makeUpdate(Grade.class, 50, this::updateGrade))
            .add(makeUpdate(LuckyNumber.class, 1, this::updateLuckyNumber))
            .add(makeUpdate(Me.class, 1, this::updateMe))
            .build();

    <T> List<T> getList(Class<T> clazz) {
        tryCreateList(clazz);
        return (List<T>) lists.get(clazz);
    }

    private void tryCreateList(Class<?> clazz) {
        if(lists.get(clazz) == null) {
            createList(clazz);
        }
    }

    private static <T> EntityUpdate<T> makeUpdate(Class<T> clazz, int count, BiFunction<T, Integer, T> update) {
        return ImmutableEntityUpdate.of(clazz, count, update);
    }

    private <T> List<T> createList(Class<T> clazz) {
        entitiesRelations.get(clazz)
                .forEach(this::tryCreateList);
        EntityUpdate<T> entityUpdate = (EntityUpdate<T>) StreamSupport.stream(updates)
                .filter(u -> u.clazz().equals(clazz))
                .findAny()
                .get();
        List<T> list = IntStreams.range(0, entityUpdate.count())
                .mapToObj(i ->
                        entityUpdate.update().apply(templates.forClass(clazz), i))
                .collect(toList());
        lists.put(clazz, list);
        return list;
    }

    private Subject updateSubject(Subject s, int index) {
        return ImmutableSubject.copyOf(s)
                .withName(SUBJECTS.get(index));
    }

    private Average updateAverage(Average a, int index) {
        return ImmutableAverage.copyOf(a)
                .withSubject(idFromIndex(Subject.class, index));
    }

    private Teacher updateTeacher(Teacher t, int index) {
        if (index == 0) {
            //One teacher without names
            return ImmutableTeacher.copyOf(t)
                    .withFirstName(Optional.absent())
                    .withLastName(Optional.absent());
        }
        return t;
    }

    private Event updateEvent(Event e, int index) {
        return ImmutableEvent.copyOf(e)
                .withAddedBy(idFromIndex(Teacher.class, index))
                .withCategory(idFromIndex(EventCategory.class, index));
    }

    private EventCategory updateEventCategory(EventCategory ec, Integer integer) {
        return ec;
    }
    private LibrusColor updateColor(LibrusColor c, Integer integer) {
        return c;
    }

    private GradeComment updateGradeComment(GradeComment c, Integer integer) {
        return c;
    }

    private Grade updateGrade(Grade g, int index) {
        return ImmutableGrade.copyOf(g)
                .withAddedById(idFromIndex(Teacher.class, index))
                .withCategoryId(idFromIndex(GradeCategory.class, index))
                .withLessonId(idFromIndex(PlainLesson.class, index))
                .withSubjectId(idFromIndex(Subject.class, index))
                .withCommentIds(multipleIds(GradeComment.class, index));
    }

    private Attendance updateAttendance(Attendance a, int index) {
        if(index % 10 == 0) {
            return ImmutableAttendance.copyOf(a)
                    .withAddedById(Optional.absent())
                    .withCategoryId(idFromIndex(AttendanceCategory.class, index))
                    .withLessonId(Optional.absent());
        }else {
            return ImmutableAttendance.copyOf(a)
                    .withAddedById(idFromIndex(Teacher.class, index))
                    .withCategoryId(idFromIndex(AttendanceCategory.class, index))
                    .withLessonId(idFromIndex(PlainLesson.class, index));
        }
    }

    private AttendanceCategory updateAttendanceCategory(AttendanceCategory a, int index) {
        return a;
    }

    private PlainLesson updatePlainLesson(PlainLesson pl, int index) {
        return ImmutablePlainLesson.copyOf(pl)
                .withTeacher(idFromIndex(Teacher.class, index))
                .withSubject(idFromIndex(Subject.class, index));
    }

    private Announcement updateAnnouncement(Announcement a, int index) {
        if(index == 1) {
            return ImmutableAnnouncement.copyOf(a)
                    .withAddedById(Optional.absent());
        }else {
            return ImmutableAnnouncement.copyOf(a)
                    .withAddedById(idFromIndex(Teacher.class, index));
        }

    }

    private GradeCategory updateGradeCategory(GradeCategory gc, int index) {
        ImmutableGradeCategory copy = ImmutableGradeCategory.copyOf(gc);
        if (index == 0) {
            return copy.withColorId(Optional.absent());
        } else {
            return copy.withColorId(idFromIndex(LibrusColor.class, index));
        }
    }

    private LuckyNumber updateLuckyNumber(LuckyNumber ln, Integer integer) {
        return ln;
    }

    private Me updateMe(Me me, Integer integer) {
        return me;
    }

    private String idFromIndex(Class<? extends Identifiable> clazz, int index) {
        List<? extends Identifiable> list = getList(clazz);
        Identifiable o = list.get(index % list.size());
        return o.id();
    }

    private List<String> multipleIds(Class<? extends Identifiable> clazz, int index) {
        return IntStreams.range(0, random.nextInt(3))
                .mapToObj(subIndex -> idFromIndex(clazz, index + subIndex))
                .collect(toList());
    }

    private interface UpdateEntityFunction<T> {
        T update(T t, int index);
    }

}
