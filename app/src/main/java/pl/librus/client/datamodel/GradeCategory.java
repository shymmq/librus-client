package pl.librus.client.datamodel;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Embedded;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;

@Entity(builder = ImmutableGradeCategory.Builder.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableGradeCategory.class)
public abstract class GradeCategory implements Persistable, Identifiable {

    @Key
    public abstract String id();

    @Value.Default
    public Integer weight(){
        return 1;
    }

    public abstract String name();

    @Embedded
    public abstract HasId color();

}
