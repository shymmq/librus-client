package pl.librus.client.datamodel.lesson;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Optional;

import org.immutables.value.Value;

import javax.persistence.Embeddable;

import pl.librus.client.datamodel.Teacher;

@Embeddable
@Value.Immutable
@JsonDeserialize(as = ImmutableLessonTeacher.class)
public abstract class LessonTeacher {

    public abstract String id();

    public abstract Optional<String> firstName();

    public abstract Optional<String> lastName();

    @Nullable
    @JsonProperty("IsSchoolAdministrator")
    public abstract Boolean schoolAdministrator();

    public static ImmutableLessonTeacher.Builder builder() {
        return ImmutableLessonTeacher.builder();
    }

    public String name() {
        if(firstName().isPresent() && lastName().isPresent()) {
            return firstName().get() + " " + lastName().get();
        } else {
            return id();
        }
    }

    public static LessonTeacher fromTeacher(Teacher teacher) {
        return builder()
                .id(teacher.id())
                .firstName(teacher.firstName())
                .lastName(teacher.lastName())
                .schoolAdministrator(teacher.schoolAdministrator())
                .build();
    }
}
