package pl.librus.client.datamodel.grade;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.List;

import io.requery.Column;
import io.requery.Key;
import io.requery.Superclass;
import pl.librus.client.api.IdDeserializer;
import pl.librus.client.datamodel.Identifiable;

/**
 * Created by robwys on 02/03/2017.
 */

@Superclass
public abstract class BaseGrade implements Identifiable {
    @Key
    public abstract String id();

    @JsonDeserialize(using = IdDeserializer.class)
    @JsonProperty("Lesson")
    @Column
    public abstract String lessonId();

    @JsonDeserialize(using = IdDeserializer.class)
    @JsonProperty("Subject")
    @Column
    public abstract String subjectId();

    @JsonDeserialize(using = IdDeserializer.class)
    @JsonProperty("Category")
    @Column
    public abstract String categoryId();

    @JsonDeserialize(using = IdDeserializer.class)
    @JsonProperty("Student")
    @Column
    public abstract String studentId();

    @JsonDeserialize(using = IdDeserializer.class)
    @JsonProperty("AddedBy")
    @Column
    public abstract String addedById();

    @Column
    public abstract String grade();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column
    public abstract LocalDate date();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    public abstract LocalDateTime addDate();

    @Column
    public abstract int semester();

    @JsonDeserialize(contentUsing = IdDeserializer.class)
    @Column
    @JsonProperty("Comments")
    public abstract List<String> commentIds();

    @JsonProperty("IsSemester")
    @Column
    public abstract Boolean semesterType();

    @JsonProperty("IsSemesterProposition")
    @Column
    public abstract Boolean semesterPropositionType();

    @JsonProperty("IsFinal")
    @Column
    public abstract Boolean finalType();

    @JsonProperty("IsFinalProposition")
    @Column
    public abstract Boolean finalPropositionType();

}
