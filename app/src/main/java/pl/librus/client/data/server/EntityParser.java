package pl.librus.client.data.server;

import android.support.annotation.StringRes;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class EntityParser {

    public static <T> List<T> parseList(String input, Class<T> clazz) {
        Optional<T[]> a = parseObject(input, getArrayClass(clazz));
        return a.<List>transform(Lists::newArrayList)
                .or(Collections::emptyList);
    }

    public static <T> Optional<T> parseObject(String input, Class<T> clazz) {
        ObjectMapper mapper = createMapper();
        JsonNode root;
        try {
            input = input.replace("\\\\\\", "\\");
            root = mapper.readTree(input);
            List<String> fieldNames = Lists.newArrayList(root.fieldNames());
            if(containsStandardFields(fieldNames)){
                JsonNode firstField = root.get(fieldNames.get(0));
                if(firstField.isTextual() && firstField.textValue().equals("Disabled")){
                    return Optional.absent();
                }
                T value = mapper.treeToValue(firstField, clazz);
                return Optional.of(value);
            }
        } catch (IOException e) {
            throw new ParseException(input, e);
        }
        JsonNode message = root.get("Message");
        if (message != null && !message.isMissingNode() && message.textValue().contains("is not active")) {
            return Optional.absent();
        }
        throw new ParseException(input, "Parsing failed");
    }

    private static boolean containsStandardFields(List<String> fieldNames) {
        int size = fieldNames.size();
        String lastField = fieldNames.get(size - 1);
        String penultimateField = fieldNames.get(size - 2);
        return "Url".equals(lastField) && "Resources".equals(penultimateField);
    }

    public static boolean isMaintenance(String message) {
        ObjectMapper mapper = createMapper();
        try {
            JsonNode root = mapper.readTree(message);
            JsonNode status = root.at("/Status");
            if (!status.isMissingNode() && status.textValue().equals("Maintenance")) {
                return true;
            }
        } catch (IOException e) {
        }
        return false;

    }

    private static ObjectMapper createMapper() {
        return new ObjectMapper()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                .configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
                .registerModule(new JodaModule())
                .registerModule(new GuavaModule());
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T[]> getArrayClass(Class<T> clazz) {
        return (Class<T[]>) Array.newInstance(clazz, 0).getClass();
    }
}
