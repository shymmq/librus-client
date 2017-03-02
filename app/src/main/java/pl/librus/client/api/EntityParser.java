package pl.librus.client.api;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EntityParser {

    public static <T> List<T> parseList(String input, String topLevelName, Class<T> clazz) {
        Optional<T[]> a = parseObject(input, topLevelName, getArrayClass(clazz));
        return a.<List>transform(Lists::newArrayList)
                .or(Collections::emptyList);
    }

    public static <T> Optional<T> parseObject(String input, String topLevelName, Class<T> clazz) {
        ObjectMapper mapper = createMapper();
        try {
            input = input.replace("\\\\\\", "\\");
            JsonNode root = mapper.readTree(input);
            TreeNode node = root.at("/" + topLevelName);
            if(!node.isMissingNode()) {
                return Optional.of(mapper.treeToValue(node, clazz));
            } else if(root.at("/Status").textValue().equals("Disabled")){
                return Optional.absent();
            } else {
                throw new RuntimeException("No root element, status not disabled");
            }
        } catch (Exception e) {
            throw new ParseException(input, e);
        }
    }

    private static ObjectMapper createMapper() {
        return new ObjectMapper()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                .registerModule(new JodaModule())
                .registerModule(new GuavaModule());
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T[]> getArrayClass(Class<T> clazz) {
        return (Class<T[]>) Array.newInstance(clazz, 0).getClass();
    }
}
