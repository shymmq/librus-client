package pl.librus.client.api;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import pl.librus.client.LibrusUtils;

public class EntityParser {

    public static <T> List<T> parseList(String input, String topLevelName, Class<T> clazz) {
        ObjectMapper mapper = createMapper();
        try {
            JsonNode root = mapper.readTree(input);
            TreeNode node = root.at("/" + topLevelName);
            return Arrays.asList(mapper.treeToValue(node, getArrayClass(clazz)));
        } catch (IOException e) {
            LibrusUtils.logError("Error parsing " + topLevelName);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static <T> T parseObject(String input, String topLevelName, Class<T> clazz) {
        ObjectMapper mapper = createMapper();
        try {
            JsonNode root = mapper.readTree(input);
            TreeNode node = root.at("/" + topLevelName);
            return mapper.treeToValue(node, clazz);
        } catch (IOException e) {
            LibrusUtils.logError("Error parsing " + topLevelName);
            e.printStackTrace();
            throw new ParseException(e);
        }
    }

    private static ObjectMapper createMapper() {
        return new ObjectMapper()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JodaModule())
                .registerModule(new GuavaModule());
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<? extends T[]> getArrayClass(Class<T> clazz) {
        return (Class<? extends T[]>) Array.newInstance(clazz, 0).getClass();
    }
}
