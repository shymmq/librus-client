package pl.librus.client.datamodel.grade;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Entity;
import io.requery.Persistable;

@Entity
@Value.Immutable
@Value.Style(builder = "new")
@JsonDeserialize(as = ImmutableGradeCategory.class)
public abstract class GradeCategory extends BaseGradeCategory implements Persistable {

    public static class Builder extends ImmutableGradeCategory.Builder {

    }

}
