package pl.librus.client.api;

import android.annotation.SuppressLint;
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

public class APIClient {
    private final Context context;
    private final OkHttpClient client = new OkHttpClient();

    APIClient(Context _context) {
        context = _context;
    }

    public static Promise<String, Integer, Integer> login(String username, String password, final Context c) {
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

            @SuppressLint("CommitPrefEdits")
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
        String TAG = "librus-client-log";
        if (text.length() > 4000) {
            Log.d(TAG, text.substring(0, 4000));
            log(text.substring(4000));
        } else
            Log.d(TAG, text);
    }

    private Promise<JSONObject, Integer, Integer> APIRequest(final String endpoint) {
        final Deferred<JSONObject, Integer, Integer> deferred = new DeferredObject<>();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String access_token = preferences.getString("access_token", "");
        String BASE_URL = "https://api.librus.pl/2.0";
        final Request request = new Request.Builder().addHeader("Authorization", "Bearer " + access_token)
                .url(BASE_URL + endpoint)
                .build();

        log("Performing APIRequest\n" +
                "Endpoint: " + endpoint + "\n" +
                "Access_token: " + access_token);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        deferred.resolve(new JSONObject(response.body().string()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    log("API Request failed\n" +
                            "Access_token: " + access_token + "\n" +
                            "Response code: " + response.code() + " " + response.message());
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
                                    log("Second attempt failed. Code " + result);

                                    deferred.reject(result);
                                }
                            });
                        }
                    }).fail(new FailCallback<Response>() {
                        @Override
                        public void onFail(Response result) {

                            //refresh failed
                            log("Refresh failed \n" +
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
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    deferred.reject(response);
                } else {
                    try {
                        JSONObject responseJSON = new JSONObject(response.body().string());

                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

                        String refresh_token_new = responseJSON.getString("refresh_token");
                        String access_token = responseJSON.getString("access_token");

                        editor.putString("refresh_token", refresh_token_new);
                        editor.putString("access_token", access_token);

                        editor.commit();
                        deferred.resolve(access_token);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        deferred.reject(response);
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

    private Promise<Map<String, String>, Integer, Integer> getTeachers() {
        final Deferred<Map<String, String>, Integer, Integer> deferred = new DeferredObject<>();

        APIRequest("/Users").then(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject result) {
                try {
                    Map<String, String> res = new HashMap<>();
                    JSONArray rawTeachers = result.getJSONArray("Users");
                    for (int i = 0; i < rawTeachers.length(); i++) {
                        JSONObject teacher = rawTeachers.getJSONObject(i);
                        res.put(teacher.getString("Id"), teacher.getString("FirstName") + " " + teacher.getString("LastName"));
                    }
                    deferred.resolve(res);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return deferred.promise();
    }

    private Promise<JSONObject, Integer, Integer> getAnnouncementEntries() {
        return APIRequest("/SchoolNotices");
    }

    Promise<List<Announcement>, Integer, Integer> getAnnouncements() {

        final Deferred<List<Announcement>, Integer, Integer> deferred = new DeferredObject<>();

        //start asynchronous tasks
        DeferredManager dm = new AndroidDeferredManager();
        dm.when(getTeachers(), getAnnouncementEntries()).done(new DoneCallback<MultipleResults>() {
            @Override
            public void onDone(MultipleResults result) {
                try {
                    JSONObject announcements = (JSONObject) result.get(1).getResult();
                    @SuppressWarnings("unchecked")
                    Map<String, String> authors = (Map<String, String>) result.get(0).getResult();
                    JSONArray announcementArray = announcements.getJSONArray("SchoolNotices");
                    List<Announcement> res = new ArrayList<>();

                    for (int announcementIndex = 0; announcementIndex < announcementArray.length(); announcementIndex++) {
                        JSONObject rawAnnouncement = announcementArray.getJSONObject(announcementIndex);

                        Integer id = rawAnnouncement.getInt("Id");
                        LocalDate startDate = LocalDate.parse(rawAnnouncement.getString("StartDate"));
                        LocalDate endDate = LocalDate.parse(rawAnnouncement.getString("EndDate"));
                        String subject = rawAnnouncement.getString("Subject");
                        String content = rawAnnouncement.getString("Content");
                        JSONObject addedBy = rawAnnouncement.getJSONObject("AddedBy");
                        String authorId = addedBy.getString("Id");
                        String author = authors.get(authorId);
                        Teacher teacher = new Teacher(author);
                        res.add(new Announcement(id, startDate, endDate, subject, teacher, content, true));
                    }
                    log("Resolved announcements:    " + res.toString());
                    deferred.resolve(res);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return deferred.promise();

        //tasks finished
    }

    Promise<LibrusAccount, Object, Object> getAccount() {
        final Deferred<LibrusAccount, Object, Object> deferred = new DeferredObject<>();
        APIRequest("/Me").done(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject result) {
                try {
                    deferred.resolve(new LibrusAccount(result));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).fail(new FailCallback<Integer>() {
            @Override
            public void onFail(Integer result) {
                deferred.reject(result);
            }
        });
        return deferred.promise();
    }

    Promise<LuckyNumber, Object, Object> getLuckyNumber() {
        final Deferred<LuckyNumber, Object, Object> deferred = new DeferredObject<>();
        APIRequest("/LuckyNumbers").done(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject result) {
                try {
                    deferred.resolve(new LuckyNumber(result));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).fail(new FailCallback<Integer>() {
            @Override
            public void onFail(Integer result) {
                deferred.reject(result);
            }
        });
        return deferred.promise();
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
}
