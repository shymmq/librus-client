package pl.librus.client.domain.attendance;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Optional;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import io.requery.Column;
import io.requery.Key;
import io.requery.Superclass;
import pl.librus.client.data.server.IdDeserializer;
import pl.librus.client.data.server.IdOptionalDeserializer;

/**
 * Created by robwys on 02/03/2017.
 */

@Superclass
public abstract class BaseAttendance {

    @Key
    public abstract String id();

    @JsonDeserialize(using = IdOptionalDeserializer.class)
    @JsonProperty("Lesson")
    @Column
    public abstract Optional<String> lessonId();

    @JsonDeserialize(using = IdOptionalDeserializer.class)
    @JsonProperty("AddedBy")
    @Column
    public abstract Optional<String> addedById();

    @JsonDeserialize(using = IdDeserializer.class)
    @JsonProperty("Type")
    @Column
    public abstract String categoryId();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "\"date\"")
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
