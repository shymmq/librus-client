package pl.librus.client.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import java.util.List;

import io.requery.Column;
import io.requery.Convert;
import io.requery.Entity;
import io.requery.Key;
import pl.librus.client.data.db.LessonRangesConverter;

/**
 * Created by robwys on 01/05/2017.
 */

@Value.Immutable
@Entity(builder = ImmutableLibrusUnit.Builder.class)
@JsonDeserialize(builder = ImmutableLibrusUnit.Builder.class)
public abstract class LibrusUnit implements Identifiable {

    @Key
    public abstract String id();

    @JsonProperty("LessonsRange")
    @Convert(LessonRangesConverter.class)
    @Column
    public abstract List<LessonRange> lessonRanges();
}
