package pl.librus.client.api;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Random;

import java8.util.stream.IntStreams;
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
        createList(Average.class, SUBJECTS.size() - 1, this::updateAverage); // some missing averages
        createList(Teacher.class, SUBJECTS.size() - 1, this::updateTeacher); // more subjects than teachers
        createList(PlainLesson.class, SUBJECTS.size(), this::updatePlainLesson);

        createList(Announcement.class, 15, this::updateAnnouncement);

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
