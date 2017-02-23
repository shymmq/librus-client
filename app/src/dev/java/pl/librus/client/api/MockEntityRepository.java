package pl.librus.client.api;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Random;

import java8.util.stream.IntStreams;
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
import pl.librus.client.datamodel.ImmutableAttendance;
import pl.librus.client.datamodel.ImmutableAverage;
import pl.librus.client.datamodel.ImmutableEvent;
import pl.librus.client.datamodel.ImmutableGrade;
import pl.librus.client.datamodel.ImmutableGradeCategory;
import pl.librus.client.datamodel.ImmutablePlainLesson;
import pl.librus.client.datamodel.ImmutableSubject;
import pl.librus.client.datamodel.LibrusColor;
import pl.librus.client.datamodel.LuckyNumber;
import pl.librus.client.datamodel.Me;
import pl.librus.client.datamodel.PlainLesson;
import pl.librus.client.datamodel.Subject;
import pl.librus.client.datamodel.Teacher;

import static java8.util.stream.Collectors.toList;
import static pl.librus.client.api.SampleValues.COLORS;
import static pl.librus.client.api.SampleValues.SUBJECTS;

/**
 * Created by szyme on 16.02.2017.
 */

class MockEntityRepository {

    private final Map<Class<?>, Object> objects = Maps.newHashMap();

    private final Map<Class<?>, List<?>> lists = Maps.newHashMap();
    private final EntityMocks templates;
    private final Random random = new Random(42);


    MockEntityRepository() {
        templates = new EntityMocks();

        createList(Subject.class, SUBJECTS.size(), this::updateSubject);
        createList(Average.class, SUBJECTS.size(), this::updateAverage);
        createList(Teacher.class, SUBJECTS.size() - 1); // more subjects than teachers
        createList(PlainLesson.class, SUBJECTS.size(), this::updatePlainLesson);

        createList(Announcement.class, 15);

        createList(AttendanceCategory.class, 3);
        createList(Attendance.class, 600, this::updateAttendance);

        createList(EventCategory.class, 3);
        createList(Event.class, 15, this::updateEvent);

        createList(LibrusColor.class, COLORS.size());
        createList(GradeComment.class, 15);
        createList(GradeCategory.class, 5, this::updateGradeCategory);
        createList(Grade.class, 50, this::updateGrade);

        objects.put(LuckyNumber.class, templates.luckyNumber());
        objects.put(Me.class, templates.me());
    }

    <T> T getObject(Class<T> clazz) {
        return (T) objects.get(clazz);
    }

    <T> List<T> getList(Class<T> clazz) {
        return (List<T>) lists.get(clazz);
    }

    private <T> void createList(Class<T> clazz, int count) {
        createList(clazz, count, (e, i) -> e);
    }

    private <T> void createList(Class<T> clazz, int count, UpdateEntityFunction<T> updateEntityFunction) {
        List<T> list = IntStreams.range(0, count)
                .mapToObj(i ->
                        updateEntityFunction.update(templates.forClass(clazz), i))
                .collect(toList());
        lists.put(clazz, list);
    }

    private Subject updateSubject(Subject s, int index) {
        return ImmutableSubject.copyOf(s)
                .withName(SUBJECTS.get(index));
    }

    private Average updateAverage(Average a, int index) {
        return ImmutableAverage.copyOf(a)
                .withSubject(idFromIndex(Subject.class, index));
    }

    private Event updateEvent(Event e, int index) {
        return ImmutableEvent.copyOf(e)
                .withAddedBy(idFromIndex(Teacher.class, index))
                .withCategory(idFromIndex(EventCategory.class, index));
    }

    private Grade updateGrade(Grade g, int index) {
        return ImmutableGrade.copyOf(g)
                .withAddedBy(idFromIndex(Teacher.class, index))
                .withCategory(idFromIndex(GradeCategory.class, index))
                .withLesson(idFromIndex(PlainLesson.class, index))
                .withSubject(idFromIndex(Subject.class, index))
                .withComments(multipleIds(GradeComment.class, index));
    }

    private Attendance updateAttendance(Attendance a, int index) {
        return ImmutableAttendance.copyOf(a)
                .withAddedBy(idFromIndex(Teacher.class, index))
                .withType(idFromIndex(AttendanceCategory.class, index))
                .withLesson(idFromIndex(PlainLesson.class, index));
    }

    private PlainLesson updatePlainLesson(PlainLesson pl, int index) {
        return ImmutablePlainLesson.copyOf(pl)
                .withTeacher(idFromIndex(Teacher.class, index))
                .withSubject(idFromIndex(Subject.class, index));
    }

    private GradeCategory updateGradeCategory(GradeCategory gc, int index) {
        return ImmutableGradeCategory.copyOf(gc)
                .withColor(idFromIndex(LibrusColor.class, index));
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
