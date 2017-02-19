package pl.librus.client.db;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import pl.librus.client.datamodel.HasId;
import pl.librus.client.datamodel.ImmutableAnnouncement;
import pl.librus.client.datamodel.ImmutableEvent;
import pl.librus.client.datamodel.ImmutableGrade;
import pl.librus.client.datamodel.ImmutableLuckyNumber;
import pl.librus.client.datamodel.ImmutableMe;
import pl.librus.client.datamodel.ImmutableSubject;
import pl.librus.client.datamodel.ImmutableTeacher;
import pl.librus.client.datamodel.LibrusAccount;
import pl.librus.client.datamodel.MultipleIds;

public class EntityTemplates {
    public static ImmutableGrade grade() {
        return ImmutableGrade.builder()
                .date(LocalDate.now())
                .addDate(LocalDateTime.now())
                .addedBy(HasId.of("12"))
                .category(HasId.of("34"))
                .finalPropositionType(false)
                .finalType(false)
                .grade("4+")
                .id("45632")
                .lesson(HasId.of("56"))
                .semester(1)
                .semesterPropositionType(false)
                .semesterType(false)
                .subject(HasId.of(subject().id()))
                .comments(MultipleIds.fromIds(Lists.newArrayList("777", "888")))
                .student(HasId.of("77779"))
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
                .addedBy(HasId.of("1575831"))
                .build();
    }

    public static ImmutableMe me() {
        return ImmutableMe.of(LibrusAccount.builder()
                .email("tompro@gmail.com")
                .firstName("Tomasz")
                .lastName("Problem")
                .login("12u")
                .build());
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
                .category(HasId.of("7323"))
                .addedBy(HasId.of("1235072"))
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
