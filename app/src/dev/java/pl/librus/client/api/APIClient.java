package pl.librus.client.api;

import android.content.Context;

import com.google.common.collect.Lists;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

import io.requery.Persistable;
import java8.util.concurrent.CompletableFuture;
import pl.librus.client.datamodel.BaseLesson;
import pl.librus.client.datamodel.ImmutableJsonLesson;
import pl.librus.client.datamodel.ImmutableLesson;
import pl.librus.client.datamodel.ImmutableLessonTeacher;
import pl.librus.client.datamodel.JsonLesson;
import pl.librus.client.datamodel.Lesson;
import pl.librus.client.datamodel.Subject;
import pl.librus.client.datamodel.Teacher;
import pl.librus.client.datamodel.Timetable;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by szyme on 14.02.2017.
 */

public class APIClient implements IAPIClient {

    private final MockEntityRepository repository = new MockEntityRepository();
    private final EntityTemplates templates = new EntityTemplates();

    public APIClient(Context _context) {

    }


    public CompletableFuture<Void> login(String username, String password) {
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<Timetable> getTimetable(LocalDate weekStart) {
        Timetable result = new Timetable();
        String[] subjects = new String[]{
                "Matematyka",
                "Projektowanie i obsługa projektów obługujących obsługę projektową amen",
                "Wychowanie chemiczne",
                "Leżakowanie",
                "Wiedza o rodznie",
                "Przygotowanie do życia w społeczeństwie",
                "Obsługa maszyn rolniczych"
        };
        for (int dayNo = DateTimeConstants.MONDAY; dayNo <= DateTimeConstants.FRIDAY; dayNo++) {
            List<List<JsonLesson>> schoolDay = new ArrayList<>();
            for (int lessonNo = 0; lessonNo < subjects.length; lessonNo++) {

                ImmutableJsonLesson lesson = templates.jsonLesson()
                        .withSubject(templates.lessonSubject().withName(subjects[lessonNo]))
                        .withDayNo(dayNo);
                schoolDay.add(newArrayList(withLessonNumber(lesson, lessonNo)));
            }
            result.put(weekStart.plusDays(dayNo - 1), schoolDay);
        }
        //weekend
        result.put(weekStart.plusDays(5), newArrayList());
        result.put(weekStart.plusDays(6), newArrayList());

        result.get(weekStart).add(newArrayList(
                withLessonNumber(cancelledLesson(), 7)
        ));

        result.get(weekStart.plusDays(1)).add(newArrayList(
                withLessonNumber(substitutionLesson(), 7)
        ));

        return CompletableFuture.completedFuture(result);
    }

    private ImmutableJsonLesson withLessonNumber(ImmutableJsonLesson lesson, int lessonNo) {
        LocalTime startTime = LocalTime.parse("08:00");
        LocalTime lessonStart = startTime.plusHours(lessonNo);
        LocalTime lessonEnd = lessonStart.plusMinutes(45);

        return lesson
                .withLessonNo(lessonNo)
                .withHourFrom(lessonStart)
                .withHourTo(lessonEnd);
    }

    private ImmutableJsonLesson cancelledLesson() {
        return templates.jsonLesson()
                .withSubject(templates.lessonSubject().withName("Alchemia"))
                .withCancelled(true);
    }

    private ImmutableJsonLesson substitutionLesson() {
        return templates.jsonLesson()
                .withTeacher(templates.lessonTeacher()
                        .withFirstName("Michał")
                        .withLastName("Oryginalny"))
                .withSubject(templates.lessonSubject().withName("Marynowanie śledzi"))
                .withSubstitutionClass(true)
                .withSubstitutionNote("Zabrakło śledzi")
                .withOrgTeacher(repository.getList(Teacher.class).get(3).id())
                .withOrgSubject(repository.getList(Subject.class).get(3).id())
                .withLessonNo(12)
                .withOrgDate(LocalDate.parse("2017-01-01"));
    }

    public <T extends Persistable> CompletableFuture<List<T>> getAll(Class<T> clazz) {
        EntityInfo info = EntityInfos.infoFor(clazz);
        if (info.single()) {
            //noinspection unchecked
            return getObject(info.endpoint(), info.topLevelName(), clazz)
                    .thenApply(Lists::newArrayList);
        } else {
            return getList(info.endpoint(), info.topLevelName(), clazz);
        }
    }

    public <T> CompletableFuture<T> getObject(String endpoint, String topLevelName, Class<T> clazz) {
        return CompletableFuture.completedFuture(repository.getObject(clazz));
    }

    public <T> CompletableFuture<List<T>> getList(String endpoint, String topLevelName, Class<T> clazz) {
        return CompletableFuture.completedFuture(repository.getList(clazz));
    }
}
