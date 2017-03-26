package pl.librus.client.api;


import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import java8.util.function.Function;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import pl.librus.client.datamodel.Average;
import pl.librus.client.datamodel.Identifiable;
import pl.librus.client.datamodel.LibrusColor;
import pl.librus.client.datamodel.PlainLesson;
import pl.librus.client.datamodel.Teacher;
import pl.librus.client.datamodel.announcement.Announcement;
import pl.librus.client.datamodel.announcement.BaseAnnouncement;
import pl.librus.client.datamodel.announcement.FullAnnouncement;
import pl.librus.client.datamodel.announcement.ImmutableFullAnnouncement;
import pl.librus.client.datamodel.attendance.Attendance;
import pl.librus.client.datamodel.attendance.AttendanceCategory;
import pl.librus.client.datamodel.attendance.BaseAttendance;
import pl.librus.client.datamodel.attendance.EnrichedAttendance;
import pl.librus.client.datamodel.attendance.FullAttendance;
import pl.librus.client.datamodel.attendance.ImmutableEnrichedAttendance;
import pl.librus.client.datamodel.attendance.ImmutableFullAttendance;
import pl.librus.client.datamodel.grade.BaseGrade;
import pl.librus.client.datamodel.grade.BaseGradeCategory;
import pl.librus.client.datamodel.grade.EnrichedGrade;
import pl.librus.client.datamodel.grade.FullGrade;
import pl.librus.client.datamodel.grade.FullGradeCategory;
import pl.librus.client.datamodel.grade.Grade;
import pl.librus.client.datamodel.grade.GradeCategory;
import pl.librus.client.datamodel.grade.GradeComment;
import pl.librus.client.datamodel.grade.ImmutableEnrichedGrade;
import pl.librus.client.datamodel.grade.ImmutableFullGrade;
import pl.librus.client.datamodel.grade.ImmutableFullGradeCategory;
import pl.librus.client.datamodel.grade.ImmutableGradeCategory;
import pl.librus.client.datamodel.subject.BaseSubject;
import pl.librus.client.datamodel.subject.FullSubject;
import pl.librus.client.datamodel.subject.ImmutableFullSubject;
import pl.librus.client.datamodel.subject.Subject;

public class BlockingLibrusData {

    private final Map<Class<?>, Map<String, ?>> objects;

    private final DataLoadStrategy strategy;

    public static BlockingLibrusData get(DataLoadStrategy strategy) {
       return new BlockingLibrusData(Maps.newHashMap(), strategy);
    }

    public static Single<BlockingLibrusData> preload(DataLoadStrategy strategy,Class<? extends Identifiable>... preloadClasses) {
        Map<Class<?>, Map<String, ?>> objects = Maps.newHashMap();

        return Observable.fromArray(preloadClasses)
                .flatMapSingle(c -> strategy.getAll(c)
                        .toMap(Identifiable::id)
                        .doOnSuccess(res -> objects.put(c, res)))
                .ignoreElements()
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
        if(objMap == null) {
            return Maps.newHashMap();
        }else {
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
        if(obj == null) {
            return strategy.getById(clazz,id).blockingGet();
        } else {
            return obj;
        }
    }

    public <T extends Identifiable> Optional<T> getOptionalById(Class<T> clazz, String id) {
        if(id.isEmpty()) {
            return Optional.absent();
        }
        T obj = getMap(clazz).get(id);
        if(obj == null) {
            return strategy.getById(clazz,id)
                    .map(Optional::of)
                    .onErrorReturn(t -> Optional.absent())
                    .blockingGet();
        } else {
            return Optional.of(obj);
        }
    }

    public <T extends Identifiable> Optional<T> getOptionalById(Class<T> clazz, Optional<String> id) {
        if(id.isPresent()) {
            return getOptionalById(clazz, id.get());
        } else {
            return Optional.absent();
        }
    }

    public <T extends Identifiable> Optional<T> getByIdIgnoreMissing(Class<T> clazz, String id) {
        T obj = getMap(clazz).get(id);
        return Optional.fromNullable(obj);
    }

    private EnrichedAttendance enrichAttendance(BaseAttendance baseAttendance) {
        return ImmutableEnrichedAttendance.builder()
                .from(baseAttendance)
                .category(getById(AttendanceCategory.class, baseAttendance.categoryId()))
                .build();
    }

    public List<EnrichedAttendance> findEnrichedAttendances() {
        return mapAll(Attendance.class, this::enrichAttendance);
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
}
