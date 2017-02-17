package pl.librus.client.datamodel;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Embedded;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;

/**
 * Created by szyme on 12.12.2016. librus-client
 */
@Entity
@Value.Immutable
@Value.Style(builder = "new")
@JsonDeserialize(as = ImmutableGradeComment.class)
public abstract class GradeComment implements Persistable, Identifiable{

    @Key
    public abstract String id();

    @Embedded
    public abstract HasId addedBy() ;

    @Embedded
    public abstract HasId grade() ;

    public abstract String text() ;

    public static class Builder extends ImmutableGradeComment.Builder {

    }
}
