package pl.librus.client.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Optional;

import org.immutables.value.Value;

import java.io.Serializable;

import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;

@Entity(builder = ImmutableTeacher.Builder.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableTeacher.class)
public abstract class Teacher implements Persistable, Serializable, Identifiable {

    @Key
    public abstract String id();

    public abstract Optional<String> firstName();

    public abstract Optional<String> lastName();

    @JsonProperty("IsSchoolAdministrator")
    @Value.Default
    public Boolean schoolAdministrator() {
        return false;
    }

    public Optional<String> name() {
        if (firstName().isPresent() && lastName().isPresent()) {
            return Optional.of(firstName().get() + " " + lastName().get());
        } else {
            return Optional.absent();
        }
    }
}
