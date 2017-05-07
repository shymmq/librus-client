package pl.librus.client.data.server;

import com.google.common.base.Optional;
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
import pl.librus.client.domain.Average;
import pl.librus.client.domain.ImmutableAverage;
import pl.librus.client.domain.ImmutableLessonRange;
import pl.librus.client.domain.ImmutableLibrusAccount;
import pl.librus.client.domain.ImmutableLibrusClass;
import pl.librus.client.domain.ImmutableLibrusColor;
import pl.librus.client.domain.ImmutableLibrusUnit;
import pl.librus.client.domain.ImmutableLuckyNumber;
import pl.librus.client.domain.ImmutableMe;
import pl.librus.client.domain.ImmutablePlainLesson;
import pl.librus.client.domain.ImmutableTeacher;
import pl.librus.client.domain.LessonRange;
import pl.librus.client.domain.LibrusClass;
import pl.librus.client.domain.LibrusColor;
import pl.librus.client.domain.LibrusUnit;
import pl.librus.client.domain.LuckyNumber;
import pl.librus.client.domain.Me;
import pl.librus.client.domain.PlainLesson;
import pl.librus.client.domain.Teacher;
import pl.librus.client.domain.announcement.Announcement;
import pl.librus.client.domain.announcement.ImmutableAnnouncement;
import pl.librus.client.domain.attendance.Attendance;
import pl.librus.client.domain.attendance.AttendanceCategory;
import pl.librus.client.domain.attendance.ImmutableAttendance;
import pl.librus.client.domain.attendance.ImmutableAttendanceCategory;
import pl.librus.client.domain.event.Event;
import pl.librus.client.domain.event.EventCategory;
import pl.librus.client.domain.event.ImmutableEvent;
import pl.librus.client.domain.event.ImmutableEventCategory;
import pl.librus.client.domain.grade.Grade;
import pl.librus.client.domain.grade.GradeCategory;
import pl.librus.client.domain.grade.GradeComment;
import pl.librus.client.domain.grade.ImmutableGrade;
import pl.librus.client.domain.grade.ImmutableGradeCategory;
import pl.librus.client.domain.grade.ImmutableGradeComment;
import pl.librus.client.domain.lesson.ImmutableJsonLesson;
import pl.librus.client.domain.lesson.ImmutableLessonSubject;
import pl.librus.client.domain.lesson.ImmutableLessonTeacher;
import pl.librus.client.domain.lesson.Lesson;
import pl.librus.client.domain.subject.ImmutableSubject;
import pl.librus.client.domain.subject.Subject;


class EntityMocks {

    private static final long SEED = 42L;
    private final Lorem lorem = new LoremIpsum(SEED);
    private final Random random = new Random(SEED);

    private LocalDate today = LocalDate.now();

    private LocalDateTime todayMorning = LocalDateTime.now().withHourOfDay(6).withMinuteOfHour(30);

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
            .put(LibrusClass.class, this::librusClass)
            .put(LibrusUnit.class, this::librusUnit)
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
        return ImmutableLessonSubject.builder()
                .id(MOCK_ID)
                .name(randomElement(SampleValues.SUBJECTS))
                .build();
    }

    ImmutableLessonTeacher lessonTeacher() {
        return ImmutableLessonTeacher.builder()
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
                .orgLesson(MOCK_ID)
                .orgSubject(MOCK_ID)
                .orgTeacherId(MOCK_ID)
                .build();
    }

    public ImmutableGrade grade() {
        return ImmutableGrade.builder()
                .id(idFor(Grade.class))
                .date(today)
                .addDate(todayMorning)
                .addedById(MOCK_ID)
                .categoryId(MOCK_ID)
                .finalPropositionType(false)
                .finalType(false)
                .grade(randomElement(SampleValues.GRADES))
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
        return ImmutableSubject.builder()
                .id(idFor(Subject.class))
                .name(randomElement(SampleValues.SUBJECTS))
                .build();
    }


    public ImmutableAnnouncement announcement() {
        LocalDate start = randomPastDate();
        return ImmutableAnnouncement.builder()
                .id(idFor(Announcement.class))
                .startDate(start)
                .endDate(start.plusDays(random.nextInt(30)))
                .subject(lorem.getWords(1, 10))
                .content(lorem.getWords(10, 500))
                .addedById(MOCK_ID)
                .build();
    }

    public ImmutableMe me() {
        return ImmutableMe.of(ImmutableLibrusAccount.builder()
                .firstName(lorem.getFirstName())
                .lastName(lorem.getLastName())
                .login("12222u")
                .build(), MOCK_ID);
    }

    public ImmutableLibrusClass librusClass() {
        return ImmutableLibrusClass.builder()
                .id(idFor(LibrusClass.class))
                .number(1)
                .symbol("a")
                .unit(MOCK_ID)
                .build();
    }

    private LessonRange lessonAt(int hour, int minutes) {
        LocalTime from = LocalTime.MIDNIGHT
                .withHourOfDay(hour)
                .withMinuteOfHour(minutes);
        LocalTime to = from.plusMinutes(45);
        return ImmutableLessonRange.of(Optional.of(from), Optional.of(to));
    }

    public ImmutableLibrusUnit librusUnit() {
        return ImmutableLibrusUnit.builder()
                .id(idFor(LibrusUnit.class))
                .addLessonRanges(
                        lessonAt(7, 0),
                        lessonAt(8, 0),
                        lessonAt(9, 0),
                        lessonAt(10, 0),
                        lessonAt(11, 0),
                        lessonAt(12, 0),
                        lessonAt(13, 0),
                        lessonAt(14, 0),
                        lessonAt(15, 0),
                        lessonAt(16, 0),
                        lessonAt(17, 0)
                )
                .build();
    }

    public ImmutableTeacher teacher() {
        return ImmutableTeacher.builder()
                .id(idFor(Teacher.class))
                .firstName(lorem.getFirstName())
                .lastName(lorem.getLastName())
                .build();
    }

    public ImmutableLuckyNumber luckyNumber() {
        return ImmutableLuckyNumber.builder()
                .luckyNumber(random.nextInt(35))
                .day(today)
                .build();
    }

    public GradeCategory gradeCategory() {
        return ImmutableGradeCategory.builder()
                .id(idFor(GradeCategory.class))
                .colorId(MOCK_ID)
                .name(lorem.getWords(1, 5))
                .weight(random.nextInt(5))
                .build();
    }

    public ImmutableGradeComment gradeComment() {
        return ImmutableGradeComment.builder()
                .id(idFor(GradeComment.class))
                .addedBy(MOCK_ID)
                .text(lorem.getWords(1, 10))
                .build();
    }

    public ImmutablePlainLesson plainLesson() {
        return ImmutablePlainLesson.builder()
                .id(idFor(PlainLesson.class))
                .subject(MOCK_ID)
                .teacher(MOCK_ID)
                .build();
    }

    public ImmutableEvent event() {
        return ImmutableEvent.builder()
                .id(idFor(Event.class))
                .addedById(MOCK_ID)
                .categoryId(MOCK_ID)
                .content(lorem.getWords(10, 200))
                .date(randomDate())
                .lessonNo(randomLessonNo())
                .build();
    }

    public ImmutableEventCategory eventCategory() {
        return ImmutableEventCategory.builder()
                .id(idFor(EventCategory.class))
                .name(lorem.getWords(1, 10))
                .build();
    }

    public ImmutableAttendance attendance() {
        return ImmutableAttendance.builder()
                .id(idFor(Attendance.class))
                .date(randomPastDate())
                .addDate(todayMorning)
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
        return ImmutableAttendanceCategory.builder()
                .id(idFor(AttendanceCategory.class))
                .colorRGB(randomElement(SampleValues.COLORS))
                .name(lorem.getWords(1, 10))
                .presenceKind(presence)
                .shortName(shortName)
                .build();
    }

    public ImmutableAverage average() {
        return ImmutableAverage.builder()
                .fullYear(randomAverage())
                .semester1(randomAverage())
                .semester2(randomAverage())
                .subject(MOCK_ID)
                .build();
    }

    public ImmutableLibrusColor librusColor() {
        return ImmutableLibrusColor.builder()
                .id(idFor(LibrusColor.class))
                .name(lorem.getWords(1))
                .rawColor(randomElement(SampleValues.COLORS))
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

    private LocalDate randomFutureDate() {
        return LocalDate.now().plusDays(random.nextInt(30));
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
