package pl.librus.client.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;
import org.joda.time.LocalDate;

import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;

@Entity(builder = ImmutableLuckyNumber.Builder.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableLuckyNumber.class)
public abstract class LuckyNumber implements Persistable, Identifiable {

    @JsonProperty("LuckyNumberDay")
    @Key
    @Value.Parameter
    public abstract LocalDate day();

    @Value.Parameter
    public abstract int luckyNumber();

    @Override
    public String id() {
        return day().toString();
    }
}
