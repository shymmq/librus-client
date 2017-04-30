package pl.librus.client.data.db;

/**
 * Created by robwys on 30/04/2017.
 */

public class EntityMissingException extends RuntimeException {
    public EntityMissingException(String source, Class<?> clazz, String id) {
        super(String.format("Entity of type %s with id %s missing from %s", clazz.getSimpleName(), id, source));
    }
}
