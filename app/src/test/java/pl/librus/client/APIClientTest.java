package pl.librus.client;

import com.google.common.io.Resources;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import io.requery.Persistable;
import pl.librus.client.api.DefaultAPIClient;
import pl.librus.client.api.EntityInfos;
import pl.librus.client.datamodel.Announcement;
import pl.librus.client.datamodel.Attendance;
import pl.librus.client.datamodel.AttendanceCategory;
import pl.librus.client.datamodel.Average;
import pl.librus.client.datamodel.EmbeddedId;
import pl.librus.client.datamodel.Event;
import pl.librus.client.datamodel.EventCategory;
import pl.librus.client.datamodel.Grade;
import pl.librus.client.datamodel.GradeCategory;
import pl.librus.client.datamodel.GradeComment;
import pl.librus.client.datamodel.HasId;
import pl.librus.client.datamodel.ImmutableAnnouncement;
import pl.librus.client.datamodel.ImmutableAttendance;
import pl.librus.client.datamodel.ImmutableAttendanceCategory;
import pl.librus.client.datamodel.ImmutableAverage;
import pl.librus.client.datamodel.ImmutableEvent;
import pl.librus.client.datamodel.ImmutableEventCategory;
import pl.librus.client.datamodel.ImmutableGrade;
import pl.librus.client.datamodel.ImmutableGradeCategory;
import pl.librus.client.datamodel.ImmutableGradeComment;
import pl.librus.client.datamodel.ImmutableJsonLesson;
import pl.librus.client.datamodel.ImmutableLessonSubject;
import pl.librus.client.datamodel.ImmutableLessonTeacher;
import pl.librus.client.datamodel.ImmutableLibrusAccount;
import pl.librus.client.datamodel.ImmutableLibrusColor;
import pl.librus.client.datamodel.ImmutableLuckyNumber;
import pl.librus.client.datamodel.ImmutableMe;
import pl.librus.client.datamodel.ImmutablePlainLesson;
import pl.librus.client.datamodel.ImmutableSubject;
import pl.librus.client.datamodel.ImmutableTeacher;
import pl.librus.client.datamodel.JsonLesson;
import pl.librus.client.datamodel.LibrusAccount;
import pl.librus.client.datamodel.LibrusColor;
import pl.librus.client.datamodel.LuckyNumber;
import pl.librus.client.datamodel.Me;
import pl.librus.client.datamodel.MultipleIds;
import pl.librus.client.datamodel.PlainLesson;
import pl.librus.client.datamodel.Subject;
import pl.librus.client.datamodel.Teacher;
import pl.librus.client.datamodel.Timetable;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


public class APIClientTest {

    @Test
    public void shouldParseTeachers() throws IOException {
        //when
        List<Teacher> res = parseList("Teachers.json", Teacher.class);

        //then
        assertThat(res, hasItem(ImmutableTeacher.builder()
            .firstName("Tomasz")
            .lastName("Problem")
            .id("12345")
            .build()));
    }

    @Test
    public void shouldParseMe() throws IOException {
        //when
        Me res = parseObject("Me.json", Me.class);

        //then
        LibrusAccount expectedAccount = ImmutableLibrusAccount.builder()
                .email("tompro@gmail.com")
                .firstName("Tomasz")
                .lastName("Problem")
                .login("12u")
                .build();
        assertThat(res, is(ImmutableMe.of(expectedAccount)));
    }

    @Test
    public void shouldParseTimetable() throws IOException {
        //when
        Timetable res = DefaultAPIClient.parseObject(readFile("Timetable.json"), "Timetable", Timetable.class);

        //then
        JsonLesson actual = res.get(LocalDate.parse("2017-01-30"))
                .get(1).get(0);
        JsonLesson expected = ImmutableJsonLesson.builder()
                .cancelled(false)
                .dayNo(1)
                .hourFrom(LocalTime.parse("08:00"))
                .hourTo(LocalTime.parse("08:45"))
                .lessonNo(1)
                .subject(ImmutableLessonSubject.builder()
                        .id("44561")
                        .name("Godzina wychowawcza")
                        .build())
                .substitutionClass(true)
                .teacher(ImmutableLessonTeacher.builder()
                        .id("1235088")
                        .firstName("Tomasz")
                        .lastName("Problem")
                        .build())
                .substitutionClass(true)
                .orgDate(LocalDate.parse("2017-02-06"))
                .orgLessonNo(2)
                .orgLesson(HasId.of("1822337"))
                .orgSubject(HasId.of("44565"))
                .orgTeacher(HasId.of("1235090"))
                .build();
        assertEquals(expected, actual);

    }

    @Test
    public void shouldParseGrades() throws IOException {
        //when
        List<Grade> res = parseList("Grades.json", Grade.class);

        //then
        assertThat(res, hasItem(ImmutableGrade.builder()
            .addDate(LocalDateTime.parse("2016-09-29T08:30:41"))
            .addedBy(HasId.of("1235106"))
            .category(HasId.of("164150"))
            .comments(MultipleIds.fromIds("834777"))
            .date(LocalDate.parse("2016-09-29"))
            .finalPropositionType(false)
            .finalType(false)
            .grade("np")
            .id("2539299")
            .lesson(HasId.of("2732545"))
            .subject(HasId.of("44569"))
            .category(HasId.of("392450"))
            .semester(1)
            .semesterPropositionType(false)
            .semesterType(false)
            .student(HasId.of("1402361"))
            .build()));
    }

    @Test
    public void shouldParseCategories() throws IOException {
        //when
        List<GradeCategory> res = parseList("GradeCategories.json", GradeCategory.class);

        //then
        assertThat(res, hasItem(ImmutableGradeCategory.builder()
            .name("sprawdzian")
            .id("164149")
            .color(HasId.of("26"))
            .weight(7)
            .build()));
    }

    @Test
    public void shouldParseComment() throws IOException {
        //when
        List<GradeComment> res = parseList("GradeComments.json", GradeComment.class);

        //then
        assertThat(res, hasItem(ImmutableGradeComment.builder()
            .id("834777")
            .text("Srodki artystycznego wyrazu")
            .grade(HasId.of("1811988"))
            .addedBy(HasId.of("1235106"))
            .build()));
    }

    @Test
    public void shouldParseLessons() throws IOException {
        //when
        List<PlainLesson> res = parseList("Lessons.json", PlainLesson.class);

        //then
        assertThat(res, hasItem(ImmutablePlainLesson.builder()
            .id("1822337")
            .teacher(HasId.of("1235090"))
            .subject(HasId.of("44565"))
            .build()));
    }

    @Test
    public void shouldParseHomeWorks() throws IOException {
        //when
        List<Event> res = parseList("HomeWorks.json", Event.class);

        //then
        assertThat(res, hasItem(ImmutableEvent.builder()
            .category(HasId.of("7323"))
            .addedBy(HasId.of("1235072"))
            .date(LocalDate.parse("2016-10-07"))
            .id("1810676")
            .content("Węglowodory.")
            .lessonNo(5)
            .build()));
    }

    @Test
    public void shouldParseHomeWorkCategories() throws IOException {
        //when
        List<EventCategory> res = parseList("HomeWorkCategories.json", EventCategory.class);

        //then
        assertThat(res, hasItem(ImmutableEventCategory.builder()
            .id("7789")
            .name("praca klasowa")
            .build()));
    }

    @Test
    public void shouldParseAttendances() throws IOException {
        //when
        List<Attendance> res = parseList("Attendances.json", Attendance.class);

        //then
        assertThat(res, hasItem(ImmutableAttendance.builder()
            .id("t403209")
            .lesson(HasId.of("2714880"))
            .date(LocalDate.parse("2016-09-29"))
            .addDate(LocalDateTime.parse("2016-09-28T16:19:22"))
            .lessonNumber(3)
            .semester(1)
            .type(HasId.of("100"))
            .addedBy(HasId.of("1234988"))
            .build()));
    }

    @Test
    public void shouldParseAttendanceTypes() throws IOException {
        //when
        List<AttendanceCategory> res = parseList("AttendanceTypes.json", AttendanceCategory.class);

        //then
        assertThat(res, hasItem(ImmutableAttendanceCategory.builder()
            .name("Spóźnienie")
            .id("2")
            .shortName("sp")
            .standard(true)
            .presenceKind(true)
            .priority(2)
            .colorRGB("FFFFFF")
            .build()));
    }

    @Test
    public void shouldParseSubject() throws IOException {
        //when
        List<Subject> res = parseList("Subjects.json", Subject.class);
        
        //then
        assertThat(res, hasItem(ImmutableSubject.builder()
            .id("44908")
            .name("Matematyka i media")
            .build()));
    }

    @Test
    public void shouldParseLuckyNumbers() throws IOException {
        //when
        LuckyNumber luckyNumber = parseObject("LuckyNumbers.json", LuckyNumber.class);

        //then
        assertThat(luckyNumber, is(ImmutableLuckyNumber.of(
                LocalDate.parse("2017-02-03"),
                13
        )));
    }

    @Test
    public void shouldParseAverages() {
        //when
        List<Average> averages = parseList("Averages.json", Average.class);

        //then
        assertThat(averages, hasItem(ImmutableAverage.builder()
            .subject(EmbeddedId.create("44555"))
            .fullYear(4.26)
            .semester1(4.29)
            .semester2(4.17)
            .build()));
    }

    @Test
    public void shouldParseColors() {
        //when
        List<LibrusColor> colors = parseList("Colors.json", LibrusColor.class);
        //then
        LibrusColor goldenrod = ImmutableLibrusColor.builder()
                .id("13")
                .name("goldenrod")
                .rawColor("DAA520")
                .build();
        assertThat(colors, hasItem(goldenrod));
    }

    @Test
    public void shouldParseAnnouncements() {
        //when
        List<Announcement> res = parseList("SchoolNotices.json", Announcement.class);

        //then
        assertThat(res, hasItem(ImmutableAnnouncement.builder()
            .id("167110")
            .startDate(LocalDate.parse("2016-09-21"))
            .endDate(LocalDate.parse("2017-06-14"))
            .subject("Konsultacje z matematyki")
            .content("Konsultacje z matematyki dla uczniów klas: 1B  2D2A 3A3D")
            .addedBy(HasId.of("1575831"))
            .build()));
    }

    private static String readFile(String fileName) {
        try {
            return Resources.toString(Resources.getResource(fileName), Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <T extends Persistable> List<T> parseList(String filename, Class<T> clazz) {
        return DefaultAPIClient.parseList(readFile(filename), EntityInfos.infoFor(clazz).topLevelName(), clazz);
    }

    private <T extends Persistable> T parseObject(String filename, Class<T> clazz) {
        return DefaultAPIClient.parseObject(readFile(filename), EntityInfos.infoFor(clazz).topLevelName(), clazz);
    }



}
