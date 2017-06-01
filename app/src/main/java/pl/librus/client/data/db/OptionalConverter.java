package pl.librus.client.data.db;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;

import java.io.IOException;

public class OptionalConverter extends JsonConverter<Optional> {
    @Override
    public Class<Optional> getMappedType() {
        return Optional.class;
    }

    @Override
    public String convertToPersisted(Optional value) {
        if (value == null || !value.isPresent()) {
            return null;
        }
        String typeName = value.get().getClass().getName();
        return typeName + ";" + convertToString(value.get());
    }

    @Override
    public Optional convertToMapped(Class<? extends Optional> type, String value) {
        if (value == null) {
            return Optional.absent();
        } else {
            int split = value.indexOf(";");
            String className = value.substring(0, split);
            String extractedValue = value.substring(split + 1);
            try {
                Class<?> extractedType = Class.forName(className);
                return Optional.fromNullable(convertFromString(extractedValue, extractedType));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected TypeReference<Optional> getReferenceType() {
        return new TypeReference<Optional>() {
        };
    }

    protected <T> T convertFromString(String s, Class<T> clazz) {
        ObjectMapper mapper = getObjectMapper();
        try {
            return mapper.readValue(s, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
