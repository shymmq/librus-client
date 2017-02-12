package pl.librus.client.db;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import pl.librus.client.datamodel.Grade;
import pl.librus.client.datamodel.HasId;
import pl.librus.client.datamodel.ImmutableGrade;
import pl.librus.client.datamodel.MultipleIds;

/**
 * Created by robwys on 12/02/2017.
 */

public class EntityTemplates {
    public static ImmutableGrade grade() {
        return new Grade.Builder()
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
                .subject(HasId.of("78"))
                .comments(MultipleIds.fromIds(Lists.newArrayList("777", "888")))
                .student(HasId.of("77779"))
                .build();
    }
}
