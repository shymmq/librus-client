package pl.librus.client.data;

import org.immutables.value.Value;
import org.joda.time.LocalDate;

import io.requery.Column;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;
import pl.librus.client.util.LibrusUtils;

/**
 * Created by robwys on 09/04/2017.
 */

@Value.Immutable
@Entity(builder = ImmutableLastUpdate.Builder.class)
public abstract class LastUpdate implements Persistable {

    @Key
    @Value.Parameter
    public abstract String name();

    @Value.Parameter
    @Column(name = "\"date\"")
    public abstract LocalDate date();

    public static LastUpdate of(Class clazz, LocalDate date) {
        return ImmutableLastUpdate.of(LibrusUtils.getClassId(clazz), date);
    }

}
