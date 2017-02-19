package pl.librus.client.sql;

import org.joda.time.LocalDate;

import io.requery.Converter;

public class LocalDateConverter implements Converter<LocalDate, String> {

    @Override
    public Class<LocalDate> getMappedType() {
        return LocalDate.class;
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
    public String convertToPersisted(LocalDate value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    @Override
    public LocalDate convertToMapped(Class<? extends LocalDate> type, String value) {
        if (value == null) {
            return null;
        }
        return LocalDate.parse(value);
    }
}