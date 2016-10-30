package pl.librus.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.jdeferred.Deferred;
import org.jdeferred.DeferredManager;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.impl.DeferredObject;
import org.jdeferred.multiple.MultipleResults;
import org.jdeferred.multiple.OneResult;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class APIClient {
    private final String BASE_URL = "https://api.librus.pl/2.0";
    private final String AUTH_URL = "https://api.librus.pl/OAuth/Token";
    private final String TAG = "librus-client-log";
    private final String auth_token = "MzU6NjM2YWI0MThjY2JlODgyYjE5YTMzZjU3N2U5NGNiNGY=";
    LibrusState state;
    private String access_token = null;
    private String refresh_token = null;
    private long valid_until = 0;
    private Context context;
    private OkHttpClient client = new OkHttpClient();
    private boolean debug = true;

    APIClient(Context _context) {
        context = _context;
    }

    static Promise<String, Integer, Integer> login(String username, String password, final Context c) {
        final String AUTH_URL = "https://api.librus.pl/OAuth/Token";
        final String auth_token = "MzU6NjM2YWI0MThjY2JlODgyYjE5YTMzZjU3N2U5NGNiNGY=";
        OkHttpClient client = new OkHttpClient();
        final Deferred<String, Integer, Integer> deferred = new DeferredObject<>();

        final Request request = new Request.Builder()
                .url(AUTH_URL)
                .header("Authorization", "Basic " + auth_token)
                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "username=" + username + "&password=" + password + "&grant_type=password&librus_long_term_token=1"))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
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

    private void log(String text) {
        if (debug) {
            Log.d(TAG, text);
        }
    }

    private Promise<JSONObject, Integer, Integer> APIRequest(final String endpoint) {
        final Deferred<JSONObject, Integer, Integer> deferred = new DeferredObject<>();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final Request request = new Request.Builder().addHeader("Authorization", "Bearer " + preferences.getString("access_token", ""))
                .url(BASE_URL + endpoint)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        deferred.resolve(new JSONObject(response.body().string()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    refreshAccess().then(new DoneCallback<String>() {
                        @Override
                        public void onDone(String result) {
                            //refresh successful
                            APIRequest(endpoint).done(new DoneCallback<JSONObject>() {
                                @Override
                                public void onDone(JSONObject result) {
                                    //second attempt successful
                                    deferred.resolve(result);
                                }
                            }).fail(new FailCallback<Integer>() {
                                @Override
                                public void onFail(Integer result) {
                                    //second attempt failed
                                    deferred.reject(result);
                                }
                            });
                        }
                    }).fail(new FailCallback<Integer>() {
                        @Override
                        public void onFail(Integer result) {
                            //refresh failed
                            deferred.reject(result);
                        }
                    });
                    deferred.reject(response.code());
                }
            }
        });
        return deferred.promise();
    }


    private Promise<String, Integer, String> refreshAccess() {
        final Deferred<String, Integer, String> deferred = new DeferredObject<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        if (prefs.getBoolean("logged_in", false)) {
            throw new Error();
        }

        final Request request = new Request.Builder()
                .url(AUTH_URL)
                .header("Authorization", "Basic " + auth_token)
                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "refresh_token=" + prefs.getString("refresh_token", null) + "&grant_type=refresh_token&librus_long_term_token=1"))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    deferred.reject(response.code());
                } else {
                    try {
                        JSONObject responseJSON = new JSONObject(response.body().string());

                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

                        editor.putString("refresh_token", responseJSON.getString("refresh_token"));
                        editor.putString("access_token", responseJSON.getString("access_token"));
                        editor.commit();
                        deferred.resolve(access_token);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return deferred.promise();
    }

    private Promise<Map<String, String>, Integer, Integer> getEventCategories() {

        final Deferred<Map<String, String>, Integer, Integer> deferred = new DeferredObject<>();

        APIRequest("/HomeWorks/Categories").then(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject result) {
                try {
                    Map<String, String> res = new HashMap<>();
                    JSONArray rawCategories = result.getJSONArray("Categories");
                    for (int i = 0; i < rawCategories.length(); i++) {
                        JSONObject category = rawCategories.getJSONObject(i);
                        res.put(category.getString("Id"), category.getString("Name"));
                    }
                    deferred.resolve(res);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return deferred.promise();
    }

    private Promise<JSONObject, Integer, Integer> getEventEntries() {
        return APIRequest("/HomeWorks");
    }

    public Promise<List<Event>, Integer, Integer> getEvents() {

        final Deferred<List<Event>, Integer, Integer> deferred = new DeferredObject<>();

        //start asynchronous tasks
        DeferredManager dm = new AndroidDeferredManager();
        dm.when(getEventEntries(), getEventCategories()).done(new DoneCallback<MultipleResults>() {
            @Override
            public void onDone(MultipleResults result) {
                try {

                    JSONObject events = (JSONObject) result.get(0).getResult();
                    @SuppressWarnings("unchecked")
                    Map<String, String> categories = (Map<String, String>) result.get(1).getResult();
                    JSONArray eventArray = events.getJSONArray("HomeWorks");
                    List<Event> res = new ArrayList<>();

                    for (int eventIndex = 0; eventIndex < eventArray.length(); eventIndex++) {

                        JSONObject rawEvent = eventArray.getJSONObject(eventIndex);

                        String category = categories.get(String.valueOf(rawEvent.getJSONObject("Category").getInt("Id")));
                        String description = rawEvent.getString("Content");
                        LocalDate date = LocalDate.parse(rawEvent.getString("Date"));
                        int lessonNumber = Integer.parseInt(rawEvent.getString("LessonNo"));
                        res.add(new Event(category, description, date, lessonNumber));
                    }
                    log("Resolved events:   " + res.toString());
                    deferred.resolve(res);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return deferred.promise();

        //tasks finished


    }

    Promise<Timetable, String, String> getTimetable(final LocalDate... weeks) {

        final Deferred<Timetable, String, String> deferred = new DeferredObject<>();

        Promise promises[] = new Promise[weeks.length];

        for (int i = 0; i < weeks.length; i++) {
            LocalDate weekStart = weeks[i];
            promises[i] = (APIRequest("/Timetables?weekStart=" + weekStart.toString("yyyy-MM-dd")));
        }

        DeferredManager dm = new AndroidDeferredManager();
        dm.when(promises).done(new DoneCallback<MultipleResults>() {
            @Override
            public void onDone(MultipleResults result) {
                Timetable timetable = new Timetable();
                try {
                    for (OneResult aResult : result) {
                        JSONObject rawData = ((JSONObject) aResult.getResult()).getJSONObject("Timetable");
                        log(rawData.toString());
                        Iterator<String> dayIterator = rawData.keys();
                        while (dayIterator.hasNext()) {
                            String key = dayIterator.next();
                            log(key);
                            timetable.addSchoolDay(new SchoolDay(rawData.getJSONArray(key), LocalDate.parse(key)));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                log("all promises resolved");
                deferred.resolve(timetable);
            }

        });

        return deferred.promise();
    }

//    void update(Runnable onSuccess) {
//        final CountDownLatch latch = new CountDownLatch(2);
//        Consumer countDown = new Consumer() {
//            @Override
//            public void run(Object result) {
//                latch.countDown();
//            }
//        };
//        getTimetable(countDown, TimetableUtils.getWeekStart(), TimetableUtils.getWeekStart().plusWeeks(1));
////        getEvents(countDown);
//        log("Waiting for all tasks to finish..");
//        try {
//            latch.await();
//            log("Finished");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
//        editor.putLong("lastUpdate", System.currentTimeMillis());
//        editor.commit();
//
//        onSuccess.run();
//    }
}
