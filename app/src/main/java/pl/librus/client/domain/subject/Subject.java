package pl.librus.client.domain.subject;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Entity;
import pl.librus.client.domain.Identifiable;

@Entity(builder = ImmutableSubject.Builder.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableSubject.class)
public abstract class Subject extends BaseSubject implements Identifiable {

}
