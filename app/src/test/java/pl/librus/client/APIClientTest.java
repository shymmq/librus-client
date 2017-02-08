package pl.librus.client;

import com.google.common.io.Resources;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import pl.librus.client.api.APIClient;
import pl.librus.client.datamodel.Attendance;
import pl.librus.client.datamodel.AttendanceCategory;
import pl.librus.client.datamodel.Average;
import pl.librus.client.datamodel.Event;
import pl.librus.client.datamodel.EventCategory;
import pl.librus.client.datamodel.Grade;
import pl.librus.client.datamodel.GradeCategory;
import pl.librus.client.datamodel.GradeComment;
import pl.librus.client.datamodel.ImmutableJsonLesson;
import pl.librus.client.datamodel.JsonLesson;
import pl.librus.client.datamodel.LessonSubject;
import pl.librus.client.datamodel.LessonTeacher;
import pl.librus.client.datamodel.LibrusColor;
import pl.librus.client.datamodel.LuckyNumber;
import pl.librus.client.datamodel.Me;
import pl.librus.client.datamodel.PlainLesson;
import pl.librus.client.datamodel.Subject;
import pl.librus.client.datamodel.Teacher;
import pl.librus.client.datamodel.Timetable;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


public class APIClientTest {
    private static String readFile(String fileName) {
        try {
            return Resources.toString(Resources.getResource(fileName), Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void shouldParseTeachers() throws IOException {
        //given
        String fileName = "Teachers.json";
        List<Teacher> res = APIClient.parseList(readFile(fileName), "Users", Teacher.class);
    }

    @Test
    public void shouldParseMe() throws IOException {
        //given
        Me res = APIClient.parseObject(readFile("Me.json"), "Me", Me.class);
    }

    @Test
    public void shouldParseTimetable() throws IOException {
        //given
        Timetable res = APIClient.parseObject(readFile("Timetable.json"), "Timetable", Timetable.class);

        //then
        JsonLesson actual = res.get(LocalDate.parse("2017-01-30"))
                .get(1).get(0);
        JsonLesson expected = ImmutableJsonLesson.builder()
                .cancelled(false)
                .dayNo(1)
                .hourFrom(LocalTime.parse("08:00"))
                .hourTo(LocalTime.parse("08:45"))
                .lessonNo(1)
                .subject(new LessonSubject.Builder()
                        .id("44561")
                        .name("Godzina wychowawcza")
                        .build())
                .substitutionClass(true)
                .teacher(new LessonTeacher.Builder()
                        .id("1235088")
                        .firstName("Tomasz")
                        .lastName("Problem")
                        .build())
                .build();
        assertEquals(expected, actual);

    }

    @Test
    public void shouldParseGrades() throws IOException {
        //given
        List<Grade> res = APIClient.parseList(readFile("Grades.json"), "Grades", Grade.class);
    }

    @Test
    public void shouldParseCategories() throws IOException {
        //given
        List<GradeCategory> res = APIClient.parseList(readFile("GradeCategories.json"), "Categories", GradeCategory.class);
    }

    @Test
    public void shouldParseComment() throws IOException {
        //given
        List<GradeComment> res = APIClient.parseList(readFile("GradeComments.json"), "Comments", GradeComment.class);
    }

    @Test
    public void shouldParseLessons() throws IOException {
        //given
        List<PlainLesson> res = APIClient.parseList(readFile("Lessons.json"), "Lessons", PlainLesson.class);
    }

    @Test
    public void shouldParseHomeWorks() throws IOException {
        //given
        List<Event> res = APIClient.parseList(readFile("HomeWorks.json"), "HomeWorks", Event.class);
        System.out.println(res);
    }

    @Test
    public void shouldParseHomeWorkCategories() throws IOException {
        //given
        List<EventCategory> res = APIClient.parseList(readFile("HomeWorkCategories.json"), "Categories", EventCategory.class);
    }

    @Test
    public void shouldParseAttendances() throws IOException {
        //given
        List<Attendance> res = APIClient.parseList(readFile("Attendances.json"), "Attendances", Attendance.class);
    }

    @Test
    public void shouldParseAttendanceTypes() throws IOException {
        //given
        List<AttendanceCategory> res = APIClient.parseList(readFile("AttendanceTypes.json"), "Types", AttendanceCategory.class);
    }

    @Test
    public void shouldParseSubject() throws IOException {
        //given
        APIClient.parseList(readFile("Subjects.json"), "Subjects", Subject.class);
    }

    @Test
    public void shouldParseLuckyNumbers() throws IOException {
        //given
        LuckyNumber luckyNumber = APIClient.parseObject(readFile("LuckyNumbers.json"), "LuckyNumber", LuckyNumber.class);
    }

    @Test
    public void shouldParseAverages() {
        List<Average> luckyNumber = APIClient.parseList(readFile("Averages.json"), "Averages", Average.class);
    }

    @Test
    public void shouldParseColors() {
        //when
        List<LibrusColor> colors = APIClient.parseList(readFile("Colors.json"), "Colors", LibrusColor.class);
        //then
        LibrusColor goldenrod = new LibrusColor.Builder()
                .id("13")
                .name("goldenrod")
                .rawColor("DAA520")
                .build();
        assertThat(colors, hasItem(goldenrod));
    }

}
