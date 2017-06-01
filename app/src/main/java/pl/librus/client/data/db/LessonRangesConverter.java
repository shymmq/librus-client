package pl.librus.client.data.db;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

import pl.librus.client.domain.LessonRange;

/**
 * Created by robwys on 01/05/2017.
 */

public class LessonRangesConverter extends JsonConverter<List<LessonRange>> {
    @Override
    public Class<List<LessonRange>> getMappedType() {
        return (Class) List.class;
    }

    @Override
    protected TypeReference<List<LessonRange>> getReferenceType() {
        return new TypeReference<List<LessonRange>>() {
        };
    }
}
