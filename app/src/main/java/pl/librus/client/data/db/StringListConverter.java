package pl.librus.client.data.db;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

import pl.librus.client.domain.LessonRange;

/**
 * Created by robwys on 04/02/2017.
 */

public class StringListConverter extends JsonConverter<List<String>> {

    @Override
    public Class<List<String>> getMappedType() {
        return (Class) List.class;
    }

    @Override
    protected TypeReference<List<String>> getReferenceType() {
        return new TypeReference<List<String>>() {};
    }
}