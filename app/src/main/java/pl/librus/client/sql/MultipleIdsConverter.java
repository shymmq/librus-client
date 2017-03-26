package pl.librus.client.sql;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;

import io.requery.Converter;

/**
 * Created by robwys on 04/02/2017.
 */

public class MultipleIdsConverter implements Converter<List, String> {

    @Override
    public Class<List> getMappedType() {
        return List.class;
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
    public String convertToPersisted(List value) {
        return value == null ? null : Joiner.on(';').join(value);
    }

    @Override
    public List convertToMapped(Class<? extends List> type, String value) {
        return value == null ? null : Lists.newArrayList(Splitter.on(";").omitEmptyStrings().split(value));
    }
}