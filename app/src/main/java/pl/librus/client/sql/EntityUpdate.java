package pl.librus.client.sql;

import org.immutables.value.Value;

import java.util.function.BiFunction;

@Value.Immutable
public interface EntityUpdate<T> {
    @Value.Parameter
    Class<T> clazz();

    @Value.Parameter
    int count();

    @Value.Parameter
    BiFunction<T, Integer, T> update();
}
