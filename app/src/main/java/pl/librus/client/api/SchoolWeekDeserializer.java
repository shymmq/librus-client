package pl.librus.client.api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import pl.librus.client.datamodel.Lesson;
import pl.librus.client.datamodel.SchoolDay;
import pl.librus.client.datamodel.SchoolWeek;

/**
 * Used to deserialize incoming jsons to SchollWeek class
 */

class SchoolWeekDeserializer extends StdDeserializer<SchoolWeek> {
    private static final long serialVersionUID = -4254324463462290764L;
    private final static DateTimeFormatter ISO = ISODateTimeFormat.date();

    SchoolWeekDeserializer() {
        this(null);
    }

    private SchoolWeekDeserializer(Class<?> vc) {
        super((Class<?>) null);
    }

    @Override
    public SchoolWeek deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        SchoolWeek result = new SchoolWeek();
        try {
            JsonNode root = p.getCodec().readTree(p);
            ObjectMapper objectMapper = new ObjectMapper()
                    .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .registerModule(new JodaModule());
            Iterator<Map.Entry<String, JsonNode>> fields = root.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> dayField = fields.next();
                LocalDate date = LocalDate.parse(dayField.getKey(), ISO);
                SchoolDay schoolDay = new SchoolDay(date);
                for (JsonNode lessonsNode : dayField.getValue()) {
                    if (lessonsNode.size() > 0) {
                        JsonNode lessonNode = lessonsNode.get(0);
                        Lesson lesson = objectMapper.treeToValue(lessonNode, Lesson.class);
                        lesson.setDate(date);
                        schoolDay.addLesson(lesson);
                    }
                }
                result.addSchoolDay(schoolDay);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
