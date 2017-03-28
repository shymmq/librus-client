package pl.librus.client.data.server;

import org.immutables.value.Value;

import java8.util.function.BiFunction;

@Value.Immutable
public interface EntityUpdate<T> {
    @Value.Parameter
    Class<T> clazz();

    @Value.Parameter
    int count();

    @Value.Parameter
    BiFunction<T, Integer, T> update();
}
