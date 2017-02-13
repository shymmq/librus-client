package pl.librus.client.sql;

import org.immutables.value.Value;

import io.requery.Persistable;

/**
 * Created by robwys on 12/02/2017.
 */

@Value.Immutable
public abstract class EntityChange<T extends Persistable>{
    public static enum Type {
        ADDED, CHANGED
    }

    @Value.Parameter
    public abstract Type type();

    @Value.Parameter
    public abstract T entity();
}
