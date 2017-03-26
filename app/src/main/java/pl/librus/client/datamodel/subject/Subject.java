package pl.librus.client.datamodel.subject;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;
import pl.librus.client.datamodel.Identifiable;

@Entity(builder = ImmutableSubject.Builder.class)
@Value.Immutable
@JsonDeserialize(as=ImmutableSubject.class)
public abstract class Subject extends BaseSubject implements Identifiable {

}
