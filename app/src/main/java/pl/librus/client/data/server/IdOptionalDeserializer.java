package pl.librus.client.data.server;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Optional;

import java.io.IOException;


public class IdOptionalDeserializer extends JsonDeserializer<Optional<String>> {

    @Override
    public Optional<String> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        return Optional.fromNullable(node.get("Id")).transform(v -> v.asText());
    }
}
