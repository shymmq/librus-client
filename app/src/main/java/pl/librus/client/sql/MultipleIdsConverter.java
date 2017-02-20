package pl.librus.client.sql;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import io.requery.Converter;
import pl.librus.client.datamodel.MultipleIds;

/**
 * Created by robwys on 04/02/2017.
 */

public class MultipleIdsConverter implements Converter<MultipleIds, String> {

    @Override
    public Class<MultipleIds> getMappedType() {
        return MultipleIds.class;
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
    public String convertToPersisted(MultipleIds value) {
        return value == null ? null : Joiner.on(';').join(value);
    }

    @Override
    public MultipleIds convertToMapped(Class<? extends MultipleIds> type, String value) {
        return value == null ? null : MultipleIds.fromIds(Splitter.on(";").split(value));
    }
}