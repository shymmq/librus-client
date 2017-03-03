package pl.librus.client.datamodel.lesson;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import javax.persistence.Embeddable;

import pl.librus.client.datamodel.Teacher;

@Embeddable
@Value.Immutable
@Value.Style(builder = "new")
@JsonDeserialize(as = ImmutableLessonTeacher.class)
public abstract class LessonTeacher {

    public abstract String id();

    public abstract String firstName();

    public abstract String lastName();

    @Nullable
    @JsonProperty("IsSchoolAdministrator")
    public abstract Boolean schoolAdministrator();

    public static class Builder extends ImmutableLessonTeacher.Builder {

    }

    public String name() {
        return firstName() != null && lastName() != null
                ? firstName() + ' ' + lastName()
                : id();
    }

    public static LessonTeacher fromTeacher(Teacher teacher) {
        return new Builder()
                .id(teacher.id())
                .firstName(teacher.firstName())
                .lastName(teacher.lastName())
                .schoolAdministrator(teacher.schoolAdministrator())
                .build();
    }
}
