//package pl.librus.client;
//
//import com.fasterxml.jackson.core.TreeNode;
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.MapperFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.joda.JodaModule;
//import com.google.common.collect.Lists;
//import com.google.common.io.Resources;
//
//import org.junit.Test;
//
//import java.io.IOException;
//import java.lang.reflect.Array;
//import java.nio.charset.Charset;
//import java.util.List;
//
//import pl.librus.client.api.MeTable;
//import pl.librus.client.datamodel.Teacher;
//import pl.librus.client.api.Timetable;
//
//
//public class APIClientTest {
//    @Test
//    public void shouldParseTeachers() throws IOException {
//        //given
//        String fileName = "Teachers.txt";
//        List<Teacher> res = parseList(readFile(fileName), "Users", Teacher.class);
//    }
//
//    @Test
//    public void shouldParseMe() throws IOException {
//        //given
//        MeTable res = parse(readFile("MeTable.txt"), "MeTable", MeTable.class);
//        System.out.println(res);
//    }
//
//    @Test
//    public void shouldParseTimetable() throws IOException {
//        //given
//        Timetable res = parse(readFile("Timetable.txt"), "Timetable", Timetable.class);
//        System.out.println(res);
//    }
//
//    public static <T> T parse(String input, String topLevelName, Class<T> clazz) {
//        ObjectMapper mapper = createMapper();
//        try {
//            JsonNode root = mapper.readTree(input);
//            TreeNode node = root.at("/" + topLevelName);
//            return mapper.treeToValue(node, clazz);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    static <T> Class<? extends T[]> getArrayClass(Class<T> clazz) {
//        return (Class<? extends T[]>) Array.newInstance(clazz, 0).getClass();
//    }
//
//    static String readFile(String fileName) {
//        try {
//            return Resources.toString(Resources.getResource(fileName), Charset.defaultCharset());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//}
