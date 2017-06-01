package pl.librus.client.data.db;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import io.requery.Converter;

/**
 * Created by robwys on 04/02/2017.
 */

public class LocalDateConverter implements Converter<LocalDate, Long> {

    @Override
    public Class<LocalDate> getMappedType() {
        return LocalDate.class;
    }

    @Override
    public Class<Long> getPersistedType() {
        return Long.class;
    }

    @Override
    public Integer getPersistedSize() {
        return null;
    }

    @Override
    public Long convertToPersisted(LocalDate value) {
        if (value == null) {
            return null;
        }
        return value.toDateTimeAtStartOfDay(DateTimeZone.UTC).getMillis();
    }

    @Override
    public LocalDate convertToMapped(Class<? extends LocalDate> type, Long value) {
        if (value == null) {
            return null;
        }
        return new LocalDate(value, DateTimeZone.UTC);
    }
}