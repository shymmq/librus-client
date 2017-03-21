package pl.librus.client.datamodel.grade;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Embedded;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;
import pl.librus.client.api.IdDeserializer;
import pl.librus.client.datamodel.Identifiable;

/**
 * Created by szyme on 12.12.2016. librus-client
 */
@Entity(builder = ImmutableGradeComment.Builder.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableGradeComment.class)
public abstract class GradeComment implements Persistable, Identifiable {

    @Key
    public abstract String id();

    @JsonDeserialize(using = IdDeserializer.class)
    public abstract String addedBy() ;

    public abstract String text() ;

}
