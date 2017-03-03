package pl.librus.client.datamodel.subject;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;

@Entity
@Value.Style(builder = "new")
@Value.Immutable
@JsonDeserialize(as=ImmutableSubject.class)
public abstract class Subject extends BaseSubject implements Persistable {

    public static class Builder extends ImmutableSubject.Builder {

    }

}
