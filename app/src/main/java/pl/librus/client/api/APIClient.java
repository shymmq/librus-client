package pl.librus.client.api;

import android.content.Context;
import android.preference.PreferenceManager;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.joda.deser.LocalTimeDeserializer;
import com.google.common.collect.ImmutableMap;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.requery.Persistable;
import java8.util.concurrent.CompletableFuture;
import pl.librus.client.LibrusUtils;
import pl.librus.client.datamodel.Announcement;
import pl.librus.client.datamodel.Attendance;
import pl.librus.client.datamodel.AttendanceCategory;
import pl.librus.client.datamodel.Average;
import pl.librus.client.datamodel.Event;
import pl.librus.client.datamodel.EventCategory;
import pl.librus.client.datamodel.Grade;
import pl.librus.client.datamodel.GradeCategory;
import pl.librus.client.datamodel.GradeComment;
import pl.librus.client.datamodel.LibrusColor;
import pl.librus.client.datamodel.LuckyNumber;
import pl.librus.client.datamodel.Me;
import pl.librus.client.datamodel.PlainLesson;
import pl.librus.client.datamodel.Subject;
import pl.librus.client.datamodel.Teacher;
import pl.librus.client.datamodel.Timetable;

import static pl.librus.client.LibrusUtils.log;

public class APIClient {

    private final Context context;

    public APIClient(Context _context) {
        context = _context;
    }

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

    @SuppressWarnings("unchecked")
    private static <T> Class<? extends T[]> getArrayClass(Class<T> clazz) {
        return (Class<? extends T[]>) Array.newInstance(clazz, 0).getClass();
    }

    private static ObjectMapper createMapper() {
        return new ObjectMapper()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JodaModule())
                .registerModule(new GuavaModule());
    }

    public CompletableFuture<Void> login(String username, String password) {
        final String AUTH_URL = "https://api.librus.pl/OAuth/Token";
        final String auth_token = "MzU6NjM2YWI0MThjY2JlODgyYjE5YTMzZjU3N2U5NGNiNGY=";
        RequestParams params = new RequestParams();
        params.add("username", username);
        params.add("password", password);
        params.add("grant_type", "password");
        params.add("librus_long_term_token", "1");
        params.add("librus_rules_accepted", "true");
        params.add("librus_mobile_rules_accepted", "true");

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", "Basic " + auth_token);
        CompletableFutureHttpResponseHandler handler = new CompletableFutureHttpResponseHandler();
        client.post(AUTH_URL, params, handler);
        return handler.getFuture()
                .thenAccept(this::saveTokens);
    }

    private void saveTokens(String response) {
        try {
            JSONObject responseJSON = new JSONObject(response);
            String access_token = responseJSON.getString("access_token");
            String refresh_token = responseJSON.getString("refresh_token");
            PreferenceManager.getDefaultSharedPreferences(context)
                    .edit()
                    .putString("refresh_token", refresh_token)
                    .putString("access_token", access_token)
                    .apply();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    private CompletableFuture<String> APIRequest(String endpoint) {
        return fetchData(endpoint)
                .exceptionally(e -> {
                    if (e instanceof HttpException) {
                        String message = e.getMessage();
                        if (message != null && message.contains("Access Token expired")) {
                            //access token expired
                            log("Retrying APIRequest " + "Endpoint: " + endpoint);
                            return refreshAccess()
                                    .thenApply((a) -> fetchData(endpoint).join())
                                    .join();
                        }
                    }
                    throw new RuntimeException(e);
                });
    }

    private CompletableFuture<String> fetchData(final String endpoint) {
        String access_token = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("access_token", "");
        String url = "https://api.librus.pl/2.0" +
                endpoint;
        log("Performing APIRequest " + "Endpoint: " + endpoint);

        CompletableFutureHttpResponseHandler handler = new CompletableFutureHttpResponseHandler();
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", "Bearer " + access_token);
        client.get(url, handler);

        return handler.getFuture();
    }

    private CompletableFuture<Void> refreshAccess() {
        String refresh_token = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("refresh_token", null);

        log("Refreshing... \n" +
                "Refresh token: " + refresh_token);

        String AUTH_URL = "https://api.librus.pl/OAuth/Token";
        String auth_token = "MzU6NjM2YWI0MThjY2JlODgyYjE5YTMzZjU3N2U5NGNiNGY=";

        RequestParams params = new RequestParams();
        params.add("grant_type", "refresh_token");
        params.add("refresh_token", refresh_token);
        CompletableFutureHttpResponseHandler handler = new CompletableFutureHttpResponseHandler();
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", "Basic " + auth_token);
        client.post(AUTH_URL, params, handler);

        return handler.getFuture()
                .thenAccept(this::saveTokens);
    }

    CompletableFuture<Void> pushDevices(final String regToken) {
        String access_token = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("access_token", "");

        String AUTH_URL = "https://api.librus.pl/2.0/PushDevices";

        RequestParams params = new RequestParams();
        params.add("provider", "Android_dru");
        params.add("device", regToken);
        CompletableFutureHttpResponseHandler handler = new CompletableFutureHttpResponseHandler();
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", "Bearer " + access_token);
        client.post(AUTH_URL, params, handler);

        return handler.getFuture()
                .thenAccept(response -> LibrusUtils.log("Device registered"));
    }

    public CompletableFuture<Timetable> getTimetable(final LocalDate weekStart) {

        String endpoint = "/Timetables?weekStart=" + weekStart.toString("yyyy-MM-dd");

        return getObject(endpoint, "Timetable", Timetable.class);
    }

    public <T> CompletableFuture<T> getObject(String endpoint, final String topLevelName, final Class<T> clazz) {
        return APIRequest(endpoint).thenApplyAsync(s -> parseObject(s, topLevelName, clazz));
    }

    public <T> CompletableFuture<List<T>> getList(String endpoint, final String topLevelName, final Class<T> clazz) {
        return APIRequest(endpoint).thenApplyAsync(s -> parseList(s, topLevelName, clazz));
    }
}