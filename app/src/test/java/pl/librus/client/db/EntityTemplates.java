package pl.librus.client.db;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import pl.librus.client.datamodel.announcement.Announcement;
import pl.librus.client.datamodel.Event;
import pl.librus.client.datamodel.ImmutableEvent;
import pl.librus.client.datamodel.ImmutableLuckyNumber;
import pl.librus.client.datamodel.ImmutableMe;
import pl.librus.client.datamodel.ImmutableTeacher;
import pl.librus.client.datamodel.LibrusAccount;
import pl.librus.client.datamodel.subject.ImmutableSubject;
import pl.librus.client.datamodel.subject.Subject;
import pl.librus.client.datamodel.Teacher;
import pl.librus.client.datamodel.announcement.ImmutableAnnouncement;
import pl.librus.client.datamodel.grade.Grade;
import pl.librus.client.datamodel.grade.ImmutableGrade;

public class EntityTemplates {
    public static ImmutableGrade grade() {
        return new Grade.Builder()
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
        return new Subject.Builder()
                .id("123")
                .name("Matematyka")
                .build();
    }


    public static ImmutableAnnouncement announcement() {
        return new Announcement.Builder()
                .id("167110")
                .startDate(LocalDate.parse("2016-09-21"))
                .endDate(LocalDate.parse("2017-06-14"))
                .subject("Tytuł ogłoszenia")
                .content("Treść ogłoszenia")
                .addedById("1575831")
                .build();
    }

    public static ImmutableMe me() {
        return ImmutableMe.of(new LibrusAccount.Builder()
                .firstName("Tomasz")
                .lastName("Problem")
                .login("12u")
                .build());
    }

    public static ImmutableTeacher teacher() {
        return new Teacher.Builder()
                .firstName("Ala")
                .lastName("Makota")
                .id("12345")
                .build();
    }

    public static ImmutableEvent event() {
        return new Event.Builder()
                .category("7323")
                .addedBy("1235072")
                .date(LocalDate.parse("2016-10-07"))
                .id("1810676")
                .content("Praca klasowa")
                .lessonNo(5)
                .build();
    }

    public static ImmutableLuckyNumber luckyNumber() {
        return ImmutableLuckyNumber.of(LocalDate.parse("2017-06-14"), 17);
    }
}
