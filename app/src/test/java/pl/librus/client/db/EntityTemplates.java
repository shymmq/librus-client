package pl.librus.client.db;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import pl.librus.client.domain.ImmutableLibrusAccount;
import pl.librus.client.domain.ImmutableLibrusUnit;
import pl.librus.client.domain.ImmutableLuckyNumber;
import pl.librus.client.domain.ImmutableMe;
import pl.librus.client.domain.ImmutableTeacher;
import pl.librus.client.domain.LessonRange;
import pl.librus.client.domain.announcement.ImmutableAnnouncement;
import pl.librus.client.domain.event.ImmutableEvent;
import pl.librus.client.domain.grade.ImmutableGrade;
import pl.librus.client.domain.lesson.ImmutableLesson;
import pl.librus.client.domain.lesson.ImmutableLessonSubject;
import pl.librus.client.domain.lesson.ImmutableLessonTeacher;
import pl.librus.client.domain.subject.ImmutableSubject;

public class EntityTemplates {
    public static ImmutableGrade grade() {
        return ImmutableGrade.builder()
                .date(LocalDate.now())
                .addDate(LocalDateTime.now())
                .addedById("12")
                .categoryId("34")
                .finalPropositionType(false)
                .finalType(false)
                .grade("4+")
                .id("45632")
                .lessonId("56")
                .semester(1)
                .semesterPropositionType(false)
                .semesterType(false)
                .subjectId(subject().id())
                .addCommentIds("777", "888")
                .studentId("77779")
                .build();
    }

    public static ImmutableSubject subject() {
        return ImmutableSubject.builder()
                .id("123")
                .name("Matematyka")
                .build();
    }


    public static ImmutableAnnouncement announcement() {
        return ImmutableAnnouncement.builder()
                .id("167110")
                .startDate(LocalDate.parse("2016-09-21"))
                .endDate(LocalDate.parse("2017-06-14"))
                .subject("Tytuł ogłoszenia")
                .content("Treść ogłoszenia")
                .addedById("1575831")
                .build();
    }

    public static ImmutableMe me() {
        return ImmutableMe.of(ImmutableLibrusAccount.builder()
                .firstName("Tomasz")
                .lastName("Problem")
                .login("12u")
                .build(), "99");
    }

    public static ImmutableTeacher teacher() {
        return ImmutableTeacher.builder()
                .firstName("Ala")
                .lastName("Makota")
                .id("12345")
                .build();
    }

    public static ImmutableEvent event() {
        return ImmutableEvent.builder()
                .categoryId("7323")
                .addedById("1235072")
                .date(LocalDate.parse("2016-10-07"))
                .id("1810676")
                .content("Praca klasowa")
                .lessonNo(5)
                .build();
    }

    public static ImmutableLessonSubject lessonSubject() {
        return ImmutableLessonSubject.builder()
                .id("123")
                .name("Matematyka")
                .build();
    }

    public static ImmutableLessonTeacher lessonTeacher() {
        return ImmutableLessonTeacher.builder()
                .id("123")
                .firstName("Ala")
                .lastName("Makota")
                .build();
    }

    public static ImmutableLesson lesson() {
        return ImmutableLesson.builder()
                .date(LocalDate.parse("2017-02-02"))
                .cancelled(false)
                .dayNo(1)
                .hourFrom(LocalTime.parse("08:00"))
                .hourTo(LocalTime.parse("08:45"))
                .lessonNo(1)
                .subject(lessonSubject())
                .substitutionClass(false)
                .teacher(lessonTeacher())
                .build();
    }

    public static ImmutableLibrusUnit unit() {
        return ImmutableLibrusUnit.builder()
                .id("83")
                .addLessonRanges(
                        LessonRange.lessonAt(7, 0),
                        LessonRange.lessonAt(8, 0),
                        LessonRange.lessonAt(9, 0),
                        LessonRange.lessonAt(10, 0),
                        LessonRange.lessonAt(11, 0),
                        LessonRange.lessonAt(12, 0),
                        LessonRange.lessonAt(13, 0),
                        LessonRange.lessonAt(14, 0),
                        LessonRange.lessonAt(15, 0),
                        LessonRange.lessonAt(16, 0),
                        LessonRange.lessonAt(17, 0)
                )
                .build();
    }


    public static ImmutableLuckyNumber luckyNumber() {
        return ImmutableLuckyNumber.of(LocalDate.parse("2017-06-14"), 17);
    }
}
