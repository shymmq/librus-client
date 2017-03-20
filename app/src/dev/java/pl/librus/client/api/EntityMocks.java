package pl.librus.client.api;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.List;
import java.util.Map;
import java.util.Random;

import java8.util.function.Supplier;
import pl.librus.client.datamodel.Average;
import pl.librus.client.datamodel.Event;
import pl.librus.client.datamodel.EventCategory;
import pl.librus.client.datamodel.ImmutableAverage;
import pl.librus.client.datamodel.ImmutableEvent;
import pl.librus.client.datamodel.ImmutableEventCategory;
import pl.librus.client.datamodel.ImmutableLibrusColor;
import pl.librus.client.datamodel.ImmutableLuckyNumber;
import pl.librus.client.datamodel.ImmutableMe;
import pl.librus.client.datamodel.ImmutablePlainLesson;
import pl.librus.client.datamodel.ImmutableTeacher;
import pl.librus.client.datamodel.LibrusAccount;
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
import pl.librus.client.datamodel.attendance.ImmutableAttendanceCategory;
import pl.librus.client.datamodel.grade.Grade;
import pl.librus.client.datamodel.grade.GradeCategory;
import pl.librus.client.datamodel.grade.GradeComment;
import pl.librus.client.datamodel.grade.ImmutableGrade;
import pl.librus.client.datamodel.grade.ImmutableGradeComment;
import pl.librus.client.datamodel.lesson.ImmutableJsonLesson;
import pl.librus.client.datamodel.lesson.ImmutableLessonSubject;
import pl.librus.client.datamodel.lesson.ImmutableLessonTeacher;
import pl.librus.client.datamodel.lesson.Lesson;
import pl.librus.client.datamodel.lesson.LessonSubject;
import pl.librus.client.datamodel.lesson.LessonTeacher;
import pl.librus.client.datamodel.subject.ImmutableSubject;
import pl.librus.client.datamodel.subject.Subject;

import static pl.librus.client.api.SampleValues.COLORS;
import static pl.librus.client.api.SampleValues.GRADES;
import static pl.librus.client.api.SampleValues.SUBJECTS;


class EntityMocks {

    private static final long SEED = 42L;
    private final Lorem lorem = new LoremIpsum(SEED);
    private final Random random = new Random(SEED);

    private Map<Class<?>, Supplier<?>> templates = ImmutableMap.<Class<?>, Supplier<?>>builder()
            .put(Announcement.class, this::announcement)
            .put(Subject.class, this::subject)
            .put(Teacher.class, this::teacher)
            .put(Grade.class, this::grade)
            .put(GradeCategory.class, this::gradeCategory)
            .put(GradeComment.class, this::gradeComment)
            .put(PlainLesson.class, this::plainLesson)
            .put(Event.class, this::event)
            .put(EventCategory.class, this::eventCategory)
            .put(Attendance.class, this::attendance)
            .put(AttendanceCategory.class, this::attendanceCategory)
            .put(Average.class, this::average)
            .put(LibrusColor.class, this::librusColor)
            .put(LuckyNumber.class, this::luckyNumber)
            .put(Me.class, this::me)
            .put(Lesson.class, this::jsonLesson)
            .build();

    private Map<Class<?>, IdGenerator> idGenerators = Maps.newHashMap();

    private final String MOCK_ID = "_mock_";

    <T> T forClass(Class<T> clazz) {
        Object instance = templates.get(clazz).get();
        if (instance != null)
            return clazz.cast(instance);
        else
            throw new RuntimeException("Mock not available for " + clazz.getSimpleName());
    }

    ImmutableLessonSubject lessonSubject() {
        return new LessonSubject.Builder()
                .id(MOCK_ID)
                .name(randomElement(SUBJECTS))
                .build();
    }

    ImmutableLessonTeacher lessonTeacher() {
        return new LessonTeacher.Builder()
                .id(MOCK_ID)
                .firstName(lorem.getFirstName())
                .lastName(lorem.getLastName())
                .build();
    }

    ImmutableJsonLesson jsonLesson() {
        return ImmutableJsonLesson.builder()
                .cancelled(false)
                .dayNo(1)
                .hourFrom(LocalTime.parse("08:00"))
                .hourTo(LocalTime.parse("08:45"))
                .lessonNo(randomLessonNo())
                .subject(lessonSubject())
                .substitutionClass(false)
                .teacher(lessonTeacher())
                .orgDate(randomDate())
                .orgLessonNo(2)
                .orgLesson(MOCK_ID)
                .orgSubject(MOCK_ID)
                .orgTeacher(MOCK_ID)
                .build();
    }

    public ImmutableGrade grade() {
        return new Grade.Builder()
                .id(idFor(Grade.class))
                .date(LocalDate.now())
                .addDate(LocalDateTime.now())
                .addedById(MOCK_ID)
                .categoryId(MOCK_ID)
                .finalPropositionType(false)
                .finalType(false)
                .grade(randomElement(GRADES))
                .lessonId(MOCK_ID)
                .semester(randomSemester())
                .semesterPropositionType(false)
                .semesterType(false)
                .subjectId(MOCK_ID)
                .addCommentIds(MOCK_ID)
                .studentId(MOCK_ID)
                .build();
    }

    public ImmutableSubject subject() {
        return new Subject.Builder()
                .id(idFor(Subject.class))
                .name(randomElement(SUBJECTS))
                .build();
    }


    public ImmutableAnnouncement announcement() {
        LocalDate start = randomPastDate();
        return new Announcement.Builder()
                .id(idFor(Announcement.class))
                .startDate(start)
                .endDate(start.plusDays(random.nextInt(30)))
                .subject(lorem.getWords(1, 10))
                .content(lorem.getWords(10, 500))
                .addedById(MOCK_ID)
                .build();
    }

    public ImmutableMe me() {
        return ImmutableMe.of(new LibrusAccount.Builder()
                .firstName(lorem.getFirstName())
                .lastName(lorem.getLastName())
                .login("12222u")
                .build());
    }

    public ImmutableTeacher teacher() {
        return new Teacher.Builder()
                .id(idFor(Teacher.class))
                .firstName(lorem.getFirstName())
                .lastName(lorem.getLastName())
                .build();
    }

    public ImmutableLuckyNumber luckyNumber() {
        return new LuckyNumber.Builder()
                .luckyNumber(random.nextInt(35))
                .day(LocalDate.now())
                .build();
    }

    public GradeCategory gradeCategory() {
        return new GradeCategory.Builder()
                .id(idFor(GradeCategory.class))
                .colorId(MOCK_ID)
                .name(lorem.getWords(1, 5))
                .weight(random.nextInt(5))
                .build();
    }

    public ImmutableGradeComment gradeComment() {
        return new GradeComment.Builder()
                .id(idFor(GradeComment.class))
                .addedBy(MOCK_ID)
                .text(lorem.getWords(1, 10))
                .build();
    }

    public ImmutablePlainLesson plainLesson() {
        return new PlainLesson.Builder()
                .id(idFor(PlainLesson.class))
                .subject(MOCK_ID)
                .teacher(MOCK_ID)
                .build();
    }

    public ImmutableEvent event() {
        return new Event.Builder()
                .id(idFor(Event.class))
                .addedBy(MOCK_ID)
                .category(MOCK_ID)
                .content(lorem.getWords(10, 200))
                .date(randomDate())
                .lessonNo(randomLessonNo())
                .build();
    }

    public ImmutableEventCategory eventCategory() {
        return new EventCategory.Builder()
                .id(idFor(EventCategory.class))
                .name(lorem.getWords(1, 10))
                .build();
    }

    public ImmutableAttendance attendance() {
        return new Attendance.Builder()
                .id(idFor(Attendance.class))
                .date(randomPastDate())
                .addDate(LocalDateTime.now())
                .addedById(MOCK_ID)
                .lessonId(MOCK_ID)
                .lessonNumber(randomLessonNo())
                .categoryId(MOCK_ID)
                .semester(randomSemester())
                .build();
    }

    public ImmutableAttendanceCategory attendanceCategory() {
        boolean presence = random.nextDouble() < 0.05;
        String shortName = presence ? "ob" :
                random.nextBoolean() ? "sp" : "nb";
        return new AttendanceCategory.Builder()
                .id(idFor(AttendanceCategory.class))
                .colorRGB(randomElement(COLORS))
                .name(lorem.getWords(1, 10))
                .presenceKind(presence)
                .priority(random.nextInt(10))
                .shortName(shortName)
                .standard(true)
                .build();
    }

    public ImmutableAverage average() {
        return new Average.Builder()
                .fullYear(randomAverage())
                .semester1(randomAverage())
                .semester2(randomAverage())
                .subject(MOCK_ID)
                .build();
    }

    public ImmutableLibrusColor librusColor() {
        return new LibrusColor.Builder()
                .id(idFor(LibrusColor.class))
                .name(lorem.getWords(1))
                .rawColor(randomElement(COLORS))
                .build();
    }

    private double randomAverage() {
        return 1 + 5 * random.nextDouble();
    }

    private LocalDate randomDate() {
        return LocalDate.now().plusDays(60).minusDays(random.nextInt(120));
    }

    private LocalDate randomPastDate() {
        return LocalDate.now().minusDays(random.nextInt(60));
    }

    private int randomLessonNo() {
        return random.nextInt(9);
    }

    private int randomSemester() {
        return random.nextInt(1) + 1;
    }

    private String idFor(Class clazz) {
        IdGenerator generator = idGenerators.get(clazz);

        if (generator == null) {
            generator = new IdGenerator(clazz);
            idGenerators.put(clazz, generator);
        }
        return generator.get();
    }

    private <T> T randomElement(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

}
