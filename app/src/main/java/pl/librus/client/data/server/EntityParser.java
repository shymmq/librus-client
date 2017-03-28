package pl.librus.client.data.server;

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
import java.util.List;

public class EntityParser {

    public static <T> List<T> parseList(String input, String topLevelName, Class<T> clazz) {
        Optional<T[]> a = parseObject(input, topLevelName, getArrayClass(clazz));
        return a.<List>transform(Lists::newArrayList)
                .or(Collections::emptyList);
    }

    public static <T> Optional<T> parseObject(String input, String topLevelName, Class<T> clazz) {
        ObjectMapper mapper = createMapper();
        JsonNode root;
        try {
            input = input.replace("\\\\\\", "\\");
            root = mapper.readTree(input);
            TreeNode node = root.at("/" + topLevelName);
            if(!node.isMissingNode()) {
                return Optional.of(mapper.treeToValue(node, clazz));
            }
        } catch (IOException e) {
            throw new ParseException(input, e);
        }
        JsonNode status = root.at("/Status");
        if (!status.isMissingNode()) {
            if (status.textValue().equals("Disabled")) {
                return Optional.absent();
            }
            if (status.textValue().equals("Maintenance")) {
                throw new MaintenanceException();
            }
        }
        JsonNode message = root.at("/Message");
        if (!message.isMissingNode() && message.textValue().contains("is not active")) {
            return Optional.absent();
        }
        throw new ParseException(input, "Parsing failed");

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
