package pl.librus.client.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;
import org.joda.time.LocalDate;

import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;

@Entity
@Value.Immutable
@Value.Style(
        builder = "new"
)
@JsonDeserialize(as = ImmutableLuckyNumber.class)
public abstract class LuckyNumber implements Persistable, Identifiable{

    @JsonProperty("LuckyNumberDay")
    @Key
    @Value.Parameter
    public abstract LocalDate day();

    @Value.Parameter
    public abstract int luckyNumber();

    public static class Builder extends ImmutableLuckyNumber.Builder {

    }

    @Override
    public String id() {
        return day().toString();
    }
}
