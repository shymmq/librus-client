package pl.librus.client.data;


import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import java8.util.function.Function;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import pl.librus.client.domain.Average;
import pl.librus.client.domain.Identifiable;
import pl.librus.client.domain.LibrusColor;
import pl.librus.client.domain.PlainLesson;
import pl.librus.client.domain.Teacher;
import pl.librus.client.domain.announcement.Announcement;
import pl.librus.client.domain.announcement.BaseAnnouncement;
import pl.librus.client.domain.announcement.FullAnnouncement;
import pl.librus.client.domain.announcement.ImmutableFullAnnouncement;
import pl.librus.client.domain.attendance.Attendance;
import pl.librus.client.domain.attendance.AttendanceCategory;
import pl.librus.client.domain.attendance.BaseAttendance;
import pl.librus.client.domain.attendance.FullAttendance;
import pl.librus.client.domain.attendance.ImmutableFullAttendance;
import pl.librus.client.domain.event.BaseEvent;
import pl.librus.client.domain.event.Event;
import pl.librus.client.domain.event.EventCategory;
import pl.librus.client.domain.event.FullEvent;
import pl.librus.client.domain.event.ImmutableFullEvent;
import pl.librus.client.domain.grade.BaseGrade;
import pl.librus.client.domain.grade.BaseGradeCategory;
import pl.librus.client.domain.grade.EnrichedGrade;
import pl.librus.client.domain.grade.FullGrade;
import pl.librus.client.domain.grade.FullGradeCategory;
import pl.librus.client.domain.grade.Grade;
import pl.librus.client.domain.grade.GradeCategory;
import pl.librus.client.domain.grade.GradeComment;
import pl.librus.client.domain.grade.ImmutableEnrichedGrade;
import pl.librus.client.domain.grade.ImmutableFullGrade;
import pl.librus.client.domain.grade.ImmutableFullGradeCategory;
import pl.librus.client.domain.lesson.EnrichedLesson;
import pl.librus.client.domain.lesson.FullLesson;
import pl.librus.client.domain.lesson.ImmutableFullLesson;
import pl.librus.client.domain.subject.BaseSubject;
import pl.librus.client.domain.subject.FullSubject;
import pl.librus.client.domain.subject.ImmutableFullSubject;
import pl.librus.client.domain.subject.Subject;

public class BlockingLibrusData {

    private final Map<Class<?>, Map<String, ?>> objects;

    private final DataLoadStrategy strategy;

    public static BlockingLibrusData get(DataLoadStrategy strategy) {
        return new BlockingLibrusData(Maps.newHashMap(), strategy);
    }

    @SafeVarargs
    public static Single<BlockingLibrusData> preload(DataLoadStrategy strategy, Class<? extends Identifiable>... preloadClasses) {
        Map<Class<?>, Map<String, ?>> objects = Maps.newHashMap();

        return Observable.fromArray(preloadClasses)
                .flatMapSingle(c -> strategy.getAll(c)
                        .toMap(Identifiable::id)
                        .doOnSuccess(res -> objects.put(c, res)))
                .ignoreElements()
                .subscribeOn(Schedulers.io())
                .toSingle(() -> new BlockingLibrusData(objects, strategy));
    }

    private BlockingLibrusData(Map<Class<?>, Map<String, ?>> objects, DataLoadStrategy strategy) {
        this.objects = objects;
        this.strategy = strategy;
    }

    private <T extends Persistable> List<T> getAll(Class<T> clazz) {
        return Lists.newArrayList(getMap(clazz).values());
    }

    private <T extends Persistable> Map<String, T> getMap(Class<T> clazz) {
        Map<String, T> objMap = (Map<String, T>) objects.get(clazz);
        if (objMap == null) {
            return Maps.newHashMap();
        } else {
            return objMap;
        }
    }

    private <T extends Identifiable> List<T> getMany(Class<T> clazz, List<String> ids) {
        return StreamSupport.stream(ids)
                .map(id -> getOptionalById(clazz, id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public <T extends Identifiable> T getById(Class<T> clazz, String id) {
        T obj = getMap(clazz).get(id);
        if (obj == null) {
            return strategy.getById(clazz, id).blockingGet();
        } else {
            return obj;
        }
    }

    public <T extends Identifiable> Optional<T> getOptionalById(Class<T> clazz, String id) {
        if (id.isEmpty()) {
            return Optional.absent();
        }
        T obj = getMap(clazz).get(id);
        if (obj == null) {
            return strategy.getById(clazz, id)
                    .map(Optional::of)
                    .onErrorReturn(t -> Optional.absent())
                    .blockingGet();
        } else {
            return Optional.of(obj);
        }
    }

    public <T extends Identifiable> Optional<T> getOptionalById(Class<T> clazz, Optional<String> id) {
        if (id.isPresent()) {
            return getOptionalById(clazz, id.get());
        } else {
            return Optional.absent();
        }
    }

    public <T extends Identifiable> Optional<T> getByIdIgnoreMissing(Class<T> clazz, String id) {
        T obj = getMap(clazz).get(id);
        return Optional.fromNullable(obj);
    }

    public List<FullAttendance> findFullAttendances() {
        return mapAll(Attendance.class, this::makeFullAttendance);
    }

    public FullAttendance makeFullAttendance(BaseAttendance baseAttendance) {
        Optional<Subject> subject = getOptionalById(PlainLesson.class, baseAttendance.lessonId())
                .transform(lesson -> getById(Subject.class, lesson.subject()));
        return ImmutableFullAttendance.builder()
                .from(baseAttendance)
                .addedBy(getOptionalById(Teacher.class, baseAttendance.addedById()))
                .category(getById(AttendanceCategory.class, baseAttendance.categoryId()))
                .subject(subject)
                .build();
    }

    public FullAnnouncement makeFullAnnouncement(BaseAnnouncement baseAnnouncement) {
        return ImmutableFullAnnouncement.builder()
                .from(baseAnnouncement)
                .addedBy(getOptionalById(Teacher.class, baseAnnouncement.addedById()))
                .build();
    }

    public List<FullAnnouncement> findFullAnnouncements() {
        return mapAll(Announcement.class, this::makeFullAnnouncement);
    }

    public List<EnrichedGrade> findEnrichedGrades() {
        return mapAll(Grade.class, this::enrichGrade);
    }

    private EnrichedGrade enrichGrade(BaseGrade grade) {
        GradeCategory category = getById(GradeCategory.class, grade.categoryId());
        return ImmutableEnrichedGrade.builder()
                .from(grade)
                .category(makeFullGradeCategory(category))
                .build();
    }

    private FullGradeCategory makeFullGradeCategory(BaseGradeCategory gradeCategory) {
        return ImmutableFullGradeCategory.builder()
                .from(gradeCategory)
                .color(getOptionalById(LibrusColor.class, gradeCategory.colorId()))
                .build();
    }

    public FullGrade makeFullGrade(EnrichedGrade grade) {
        return ImmutableFullGrade.builder()
                .from(grade)
                .category(grade.category())
                .comments(getMany(GradeComment.class, grade.commentIds()))
                .addedBy(getOptionalById(Teacher.class, grade.addedById()))
                .subject(getById(Subject.class, grade.subjectId()))
                .build();
    }

    public FullLesson makeFullLesson(EnrichedLesson lesson) {
        return ImmutableFullLesson.builder()
                .from(lesson)
                .date(lesson.date())
                .orgTeacher(getOptionalById(Teacher.class, lesson.orgTeacherId()))
                .event(lesson.event())
                .build();
    }

    private <T extends Persistable, S> List<S> mapAll(Class<T> clazz, Function<T, S> transform) {
        return StreamSupport.stream(getAll(clazz))
                .map(transform)
                .collect(Collectors.toList());
    }

    public List<FullSubject> findFullSubjects() {
        return mapAll(Subject.class, this::makeFullSubject);
    }

    private FullSubject makeFullSubject(BaseSubject subject) {
        return ImmutableFullSubject.builder()
                .from(subject)
                .average(getByIdIgnoreMissing(Average.class, subject.id()))
                .build();
    }

    public List<FullEvent> findFullEvents() {
        return mapAll(Event.class, this::makeFullEvent);
    }

    public FullEvent makeFullEvent(BaseEvent event) {
        return ImmutableFullEvent.builder()
                .from(event)
                .addedBy(getById(Teacher.class, event.addedById()))
                .category(getById(EventCategory.class, event.categoryId()))
                .build();
    }
}
