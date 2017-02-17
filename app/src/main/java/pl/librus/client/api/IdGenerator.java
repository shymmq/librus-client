package pl.librus.client.api;

import java8.util.function.Supplier;

/**
 * Created by szyme on 16.02.2017.
 * Supplier for unique ids based on class prefix and sequential integer
 */

class IdGenerator implements Supplier<String> {

    private final String prefix;
    private int id = 0;


    public IdGenerator(String prefix) {
        this.prefix = prefix;
    }

    IdGenerator(Class<?> clazz) {
        this.prefix = clazz.getSimpleName();
    }

    @Override
    public String get() {
        return prefix + "_" + id++;
    }
}
