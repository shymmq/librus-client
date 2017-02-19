package pl.librus.client.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import javax.persistence.Embeddable;

@Embeddable
@Value.Immutable
@JsonDeserialize(as = ImmutableLessonTeacher.class)
public abstract class LessonTeacher {

    public abstract String id();

    public abstract String firstName();

    public abstract String lastName();

    @JsonProperty("IsSchoolAdministrator")
    @Value.Default
    public Boolean schoolAdministrator() {
        return false;
    }

    public static ImmutableLessonTeacher.Builder builder() {
        return ImmutableLessonTeacher.builder();
    }

    public String name() {
        return firstName() != null && lastName() != null
                ? firstName() + ' ' + lastName()
                : id();
    }
}
