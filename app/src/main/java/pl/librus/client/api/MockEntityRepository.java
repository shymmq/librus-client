package pl.librus.client.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;

import java.util.List;
import java.util.Map;

import io.requery.Persistable;
import java8.util.function.Supplier;
import java8.util.stream.Collectors;
import java8.util.stream.IntStreams;
import pl.librus.client.datamodel.Announcement;
import pl.librus.client.datamodel.Attendance;
import pl.librus.client.datamodel.AttendanceCategory;
import pl.librus.client.datamodel.Grade;
import pl.librus.client.datamodel.GradeCategory;
import pl.librus.client.datamodel.GradeComment;
import pl.librus.client.datamodel.Identifiable;
import pl.librus.client.datamodel.ImmutableAttendance;
import pl.librus.client.datamodel.ImmutableGrade;
import pl.librus.client.datamodel.ImmutableGradeCategory;
import pl.librus.client.datamodel.ImmutablePlainLesson;
import pl.librus.client.datamodel.LibrusColor;
import pl.librus.client.datamodel.PlainLesson;
import pl.librus.client.datamodel.Subject;
import pl.librus.client.datamodel.Teacher;

/**
 * Created by szyme on 16.02.2017.
 */

class MockEntityRepository {

    private final Map<Class<?>, Object> objects = Maps.newHashMap();

    private final Map<Class<?>, List<?>> lists = Maps.newHashMap();
    private final EntityTemplates templates;

    MockEntityRepository() {
        templates = new EntityTemplates();
        for (Map.Entry<Class<? extends Persistable>, EntityInfo> e : EntityInfos.all().entrySet()) {
            Class clazz = e.getKey();
            boolean single = e.getValue().single();
            if (!single) {
                lists.put(clazz, createList(10, () -> templates.forClass(clazz)));
            } else {
                objects.put(clazz, templates.forClass(clazz));
            }
        }

        addLongAnnouncement();

        updateConnections(Attendance.class, this::updateAttendance);
        updateConnections(PlainLesson.class, this::updatePlainLesson);
        updateConnections(Grade.class, this::updateGrade);
        updateConnections(GradeCategory.class, this::updateGradeCategory);
    }

    <T> T getObject(Class<T> clazz) {
        return (T) objects.get(clazz);
    }

    <T> List<T> getList(Class<T> clazz) {
        return (List<T>) lists.get(clazz);
    }

    private <T> void updateConnections(Class<T> clazz, UpdateEntityFunction<T> updateEntityFunction) {
        List<T> l = getList(clazz);
        List<T> newList = IntStreams.range(0, l.size())
                .mapToObj(i -> updateEntityFunction.update(l.get(i), i))
                .collect(Collectors.toList());
        lists.put(clazz, newList);
    }

    private <T> List<T> createList(int count, Supplier<T> supplier) {
        return IntStreams.range(0, count)
                .mapToObj(i -> supplier.get())
                .collect(Collectors.toList());
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

    private void addLongAnnouncement() {
        Lorem lorem = LoremIpsum.getInstance();
        getList(Announcement.class)
                .add(templates.announcement()
                        .withContent(lorem.getWords(2000))
                        .withSubject("To jest bardzo długie ogłoszenie, którego tytuł może się nie zmieścić na ekranie. Dziękuję, dobranoc."));
    }

    private String idFromIndex(Class<? extends Identifiable> clazz, int index) {
        List<? extends Identifiable> list = getList(clazz);
        Identifiable o = list.get(index % list.size());
        return o.id();
    }

    private List<String> multipleIds(Class<? extends Identifiable> clazz, int index) {
        return Lists.newArrayList(idFromIndex(clazz, index));
    }

    private interface UpdateEntityFunction<T> {
        T update(T t, int index);
    }

}
