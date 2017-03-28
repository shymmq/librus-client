package pl.librus.client.domain.grade;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Entity;
import io.requery.Persistable;

@Entity(builder = ImmutableGradeCategory.Builder.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableGradeCategory.class)
public abstract class GradeCategory extends BaseGradeCategory implements Persistable {

}
