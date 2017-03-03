package pl.librus.client.datamodel.attendance;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import io.requery.Column;
import io.requery.Key;
import io.requery.Superclass;
import pl.librus.client.api.IdDeserializer;

/**
 * Created by robwys on 02/03/2017.
 */

@Superclass
public abstract class BaseAttendance {

    @Key
    public abstract String id();

    @JsonDeserialize(using = IdDeserializer.class)
    @JsonProperty("Lesson")
    @Column
    public abstract String lessonId();

    @JsonDeserialize(using = IdDeserializer.class)
    @JsonProperty("AddedBy")
    @Column
    public abstract String addedById();

    @JsonDeserialize(using = IdDeserializer.class)
    @JsonProperty("Type")
    @Column
    public abstract String categoryId();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column
    public abstract LocalDate date();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    public abstract LocalDateTime addDate();

    @JsonProperty("LessonNo")
    @Column
    public abstract int lessonNumber();

    @Column
    public abstract int semester();
}
