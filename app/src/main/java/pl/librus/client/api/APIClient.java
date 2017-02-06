package pl.librus.client.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.collect.Lists;

import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.librus.client.LibrusUtils;
import pl.librus.client.datamodel.Timetable;

import static pl.librus.client.LibrusUtils.log;

public class APIClient {
    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "librus-client-logError";
    private final Context context;
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build();

    public APIClient(Context _context) {
        context = _context;


    }

    public static Promise<String, Integer, Void> login(String username, String password, final Context c) {
        final String AUTH_URL = "https://api.librus.pl/OAuth/Token";
        final String auth_token = "MzU6NjM2YWI0MThjY2JlODgyYjE5YTMzZjU3N2U5NGNiNGY=";
        OkHttpClient client = new OkHttpClient();
        final Deferred<String, Integer, Void> deferred = new DeferredObject<>();

        final Request request = new Request.Builder()
                .url(AUTH_URL)
                .header("Authorization", "Basic " + auth_token)
                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),
                        "username=" + username +
                                "&password=" + password +
                                "&grant_type=password" +
                                "&librus_long_term_token=1" +
                                "&librus_rules_accepted=true" +
                                "&librus_mobile_rules_accepted=true"))                  //TODO display popup with agreement
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @SuppressLint("CommitPrefEdits")
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    try {
                        JSONObject responseJSON = new JSONObject(response.body().string());

                        String access_token = responseJSON.getString("access_token");
                        String refresh_token = responseJSON.getString("refresh_token");


                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(c).edit();
                        editor.putString("refresh_token", refresh_token);
                        editor.putString("access_token", access_token);
                        editor.putBoolean("logged_in", true);
                        editor.commit();

                        deferred.resolve(access_token);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    deferred.reject(response.code());
                }
            }
        });
        return deferred.promise();
    }

    public static <T> List<T> parseList(String input, String topLevelName, Class<T> clazz) {
        ObjectMapper mapper = createMapper();
        try {
            JsonNode root = mapper.readTree(input);
            TreeNode node = root.at("/" + topLevelName);
            //noinspection unchecked
            return Lists.newArrayList(mapper.treeToValue(node, getArrayClass(clazz)));
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
            //noinspection unchecked
            return mapper.treeToValue(node, clazz);
        } catch (IOException e) {
            LibrusUtils.logError("Error parsing " + topLevelName);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<? extends T[]> getArrayClass(Class<T> clazz) {
        return (Class<? extends T[]>) Array.newInstance(clazz, 0).getClass();
    }

    private static ObjectMapper createMapper() {

        SimpleModule schoolWeekModule = new SimpleModule();
        return new ObjectMapper()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JodaModule())
                .registerModule(new GuavaModule())
                .registerModule(schoolWeekModule);
    }

    private Promise<JSONObject, Integer, Void> APIRequest(final String endpoint) {
        final Deferred<JSONObject, Integer, Void> deferred = new DeferredObject<>();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String access_token = preferences.getString("access_token", "");
        String url;
        String BASE_URL = "https://api.librus.pl/2.0";
        url = BASE_URL + endpoint;
        final Request request = new Request.Builder().addHeader("Authorization", "Bearer " + access_token)
                .url(url)
                .build();
        log("Performing APIRequest " +
                "Endpoint: " + endpoint);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                deferred.reject(0);
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        deferred.resolve(new JSONObject(response.body().string()));
                        log("API Request " + endpoint + " successful.");
                        response.body().close();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    LibrusUtils.logError("API Request failed\n" +
                                    "Edpoint: " + endpoint + "\n" +
                                    "Access_token: " + access_token + "\n" +
                                    "Response code: " + response.code() + " " + response.message() + "\n" +
                                    "Response: " + response.body().string());
                    refreshAccess().then(new DoneCallback<String>() {
                        @Override
                        public void onDone(String result) {

                            //refresh successful
                            log("Refresh successful");

                            APIRequest(endpoint).done(new DoneCallback<JSONObject>() {
                                @Override
                                public void onDone(JSONObject result) {

                                    //second attempt successful
                                    log("Second attempt successful");

                                    deferred.resolve(result);
                                }
                            }).fail(new FailCallback<Integer>() {
                                @Override
                                public void onFail(Integer result) {

                                    //second attempt failed
                                    LibrusUtils.logError("Second attempt failed. Code " + result);

                                    deferred.reject(result);
                                }
                            });
                        }
                    }).fail(new FailCallback<Response>() {
                        @Override
                        public void onFail(Response result) {

                            //refresh failed
                            LibrusUtils.logError("Refresh failed \n" +
                                            "Response code: " + result + " " + response.message());

                            deferred.reject(result.code());
                        }
                    });
                }
            }
        });
        return deferred.promise();
    }

    private Promise<String, Response, String> refreshAccess() {
        final Deferred<String, Response, String> deferred = new DeferredObject<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (!prefs.getBoolean("logged_in", false)) {
            throw new Error("Client not logged in. Call APIClient.login() first.");
        }

        String refresh_token = prefs.getString("refresh_token", null);

        log("Refreshing... \n" +
                "Refresh token: " + refresh_token);

        String AUTH_URL = "https://api.librus.pl/OAuth/Token";
        String auth_token = "MzU6NjM2YWI0MThjY2JlODgyYjE5YTMzZjU3N2U5NGNiNGY=";
        final Request request = new Request.Builder()
                .url(AUTH_URL)
                .header("Authorization", "Basic " + auth_token)
                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "&grant_type=refresh_token&refresh_token=" + refresh_token))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    LibrusUtils.logError("Refresh token request failed: \n" +
                                    "code: " + response.code() + "\n" +
                                    "response: " + response.body().string());
                    deferred.reject(response);
                } else {
                    JSONObject responseJSON = null;
                    try {
                        responseJSON = new JSONObject(response.body().string());

                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

                        String refresh_token_new = responseJSON.getString("refresh_token");
                        String access_token = responseJSON.getString("access_token");

                        editor.putString("refresh_token", refresh_token_new);
                        editor.putString("access_token", access_token);

                        editor.apply();
                        deferred.resolve(access_token);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LibrusUtils.logError("Refresh token request failed: \n" +
                                        "code: " + response.code() + "\n" +
                                        "response: " + (responseJSON == null ? "null" : responseJSON.toString()));
                        deferred.reject(response);
                    }
                }
            }
        });
        return deferred.promise();
    }

    Promise<Integer, Integer, Void> pushDevices(final String regToken) {
        final Deferred<Integer, Integer, Void> deferred = new DeferredObject<>();
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            final String access_token = preferences.getString("access_token", "");

            JSONObject params = new JSONObject();
            params.put("provider", "Android_dru");
            params.put("device", regToken);
            RequestBody body = RequestBody.create(JSON, params.toString());
            String AUTH_URL = "https://api.librus.pl/2.0/PushDevices";
            final Request request = new Request.Builder()
                    .addHeader("Authorization", "Bearer " + access_token)
                    .url(AUTH_URL)
                    .post(body)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    deferred.reject(0);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful())
                        deferred.resolve(response.code());
                    else
                        deferred.reject(response.code());
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            deferred.reject(1);
        }
        return deferred.promise();
    }

    public Promise<Timetable, Void, Void> getTimetable(final LocalDate weekStart) {

        String endpoint = "/Timetables?weekStart=" + weekStart.toString("yyyy-MM-dd");

        return getObject(endpoint, "Timetable", Timetable.class);
    }

    public <T> Promise<T, Void, Void> getObject(String endpoint, final String topLevelName, final Class<T> clazz) {
        final Deferred<T, Void, Void> deferred = new DeferredObject<>();
        APIRequest(endpoint).done(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject json) {
                T result = parseObject(json.toString(), topLevelName, clazz);
                deferred.resolve(result);
            }
        });
        return deferred.promise();
    }

    public <T> Promise<List<T>, Void, Void> getList(String endpoint, final String topLevelName, final Class<T> clazz) {
        final Deferred<List<T>, Void, Void> deferred = new DeferredObject<>();
        APIRequest(endpoint).done(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject result) {
                deferred.resolve(parseList(result.toString(), topLevelName, clazz));
            }
        });
        return deferred.promise();
    }
}