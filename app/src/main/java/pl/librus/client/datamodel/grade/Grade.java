package pl.librus.client.datamodel.grade;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.List;

import io.requery.Column;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;
import pl.librus.client.api.IdDeserializer;
import pl.librus.client.datamodel.Identifiable;

/**
 * Created by szyme on 08.12.2016. librus-client
 */
@Entity
@Value.Immutable
@Value.Style(builder = "new")
@JsonDeserialize(as = ImmutableGrade.class)
public abstract class Grade extends BaseGrade implements Persistable {

    public static class Builder extends ImmutableGrade.Builder {

    }
}
