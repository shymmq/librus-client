package pl.librus.client.data.db;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.base.Splitter;

import java.io.IOException;
import java.util.List;

import io.requery.Converter;

/**
 * Created by robwys on 01/05/2017.
 */

public abstract class JsonConverter <T> implements Converter<T, String> {

    @Override
    public Class<String> getPersistedType() {
        return String.class;
    }

    @Override
    public Integer getPersistedSize() {
        return null;
    }

    @Override
    public String convertToPersisted(T value) {
        if(value == null) {
            return null;
        }
        return convertToString(value);
    }

    @Override
    public T convertToMapped(Class<? extends T> type, String value) {
        if(value == null) {
            return null;
        }
        return convertFromString(value);
    }

    protected ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new GuavaModule());
        mapper.registerModule(new JodaModule());
        return mapper;
    }

    protected String convertToString(Object o) {
        ObjectMapper mapper = getObjectMapper();
        try {
            return mapper.writeValueAsString(o);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract TypeReference<T> getReferenceType();

    protected T convertFromString(String s) {
        ObjectMapper mapper = getObjectMapper();
        try {
            return mapper.readValue(s, getReferenceType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
