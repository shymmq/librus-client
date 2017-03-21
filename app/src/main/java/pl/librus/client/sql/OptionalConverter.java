package pl.librus.client.sql;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.base.Optional;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import io.requery.Converter;

public class OptionalConverter implements Converter<Optional, String> {
    @Override
    public Class<Optional> getMappedType() {
        return Optional.class;
    }

    @Override
    public Class<String> getPersistedType() {
        return String.class;
    }

    @Override
    public Integer getPersistedSize() {
        return null;
    }

    @Override
    public String convertToPersisted(Optional value) {
        return convertToString(value);
    }

    @Override
    public Optional<?> convertToMapped(Class<? extends Optional> type, String value) {
        if(value == null) {
            return Optional.absent();
        }else {
            return convertFromString(value, type);
        }
    }

    private String convertToString(Object o) {
        ObjectMapper mapper = getObjectMapper();
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    private ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new GuavaModule());
        return mapper;
    }

    private <T> T convertFromString(String s, Class<T> type) {
        ObjectMapper mapper = getObjectMapper();
        try {
            return mapper.readValue(s, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
