package pl.librus.client.datamodel;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import io.requery.Column;
import io.requery.Embedded;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;

/**
 * Created by szyme on 08.12.2016. librus-client
 */
@Entity(builder = ImmutableGrade.Builder.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableGrade.class)
public abstract class Grade implements Persistable, Identifiable {
    @Key
    public abstract String id();

    @Embedded
    public abstract HasId lesson();

    @Embedded
    public abstract HasId subject();

    @Embedded
    public abstract HasId category();

    @Embedded
    public abstract HasId student();

    @Embedded
    public abstract HasId addedBy();

    public abstract String grade();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public abstract LocalDate date();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    public abstract LocalDateTime addDate();

    public abstract int semester();

    @Nullable
    public abstract MultipleIds comments();

    @JsonProperty("IsSemester")
    public abstract Boolean semesterType();

    @JsonProperty("IsSemesterProposition")
    public abstract Boolean semesterPropositionType();

    @JsonProperty("IsFinal")
    public abstract Boolean finalType();

    @JsonProperty("IsFinalProposition")
    public abstract Boolean finalPropositionType();

    public GradeType type() {
        if (semesterPropositionType()) return GradeType.SEMESTER_PROPOSITION;
        else if (semesterType()) return GradeType.SEMESTER;
        else if (finalPropositionType()) return GradeType.FINAL_PROPOSITION;
        else if (finalType()) return GradeType.FINAL;
        else return GradeType.NORMAL;
    }

    public enum GradeType {
        NORMAL, SEMESTER_PROPOSITION, SEMESTER, FINAL_PROPOSITION, FINAL
    }

}
