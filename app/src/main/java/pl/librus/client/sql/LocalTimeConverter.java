package pl.librus.client.sql;

import org.joda.time.LocalTime;

import io.requery.Converter;

public class LocalTimeConverter implements Converter<LocalTime, Integer> {

    @Override
    public Class<LocalTime> getMappedType() {
        return LocalTime.class;
    }

    @Override
    public Class<Integer> getPersistedType() {
        return Integer.class;
    }

    @Override
    public Integer getPersistedSize() {
        return null;
    }

    @Override
    public Integer convertToPersisted(LocalTime value) {
        if (value == null) {
            return null;
        }
        return value.getMillisOfDay();

    }

    @Override
    public LocalTime convertToMapped(Class<? extends LocalTime> type, Integer value) {
        if (value == null) {
            return null;
        }
        return LocalTime.fromMillisOfDay(value);
    }
}