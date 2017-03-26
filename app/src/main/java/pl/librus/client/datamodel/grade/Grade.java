package pl.librus.client.datamodel.grade;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Entity;
import pl.librus.client.datamodel.Identifiable;

/**
 * Created by szyme on 08.12.2016. librus-client
 */
@Entity(builder = ImmutableGrade.Builder.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableGrade.class)
public abstract class Grade extends BaseGrade implements Identifiable {

}
