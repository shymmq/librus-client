package pl.librus.client.sql;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
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
        if(value == null || !value.isPresent()) {
            return null;
        }
        String typeName = value.get().getClass().getName();
        return typeName + ";" + convertToString(value.get());
    }

    @Override
    public Optional convertToMapped(Class<? extends Optional> type, String value) {
        if(value == null) {
            return Optional.absent();
        }else {
            int split = value.indexOf(";");
            String className = value.substring(0, split);
            String extractedValue = value.substring(split+1);
            try {
                Class<?> extractedType = Class.forName(className);
                return Optional.fromNullable(convertFromString(extractedValue, extractedType));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new GuavaModule());
        mapper.registerModule(new JodaModule());
        return mapper;
    }

    private String convertToString(Object o) {
        ObjectMapper mapper = getObjectMapper();
        try {
            return mapper.writeValueAsString(o);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Object convertFromString(String s, Class<?> type) {
        ObjectMapper mapper = getObjectMapper();
        try {
            return mapper.readValue(s, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
