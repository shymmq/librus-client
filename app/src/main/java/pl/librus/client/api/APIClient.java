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
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class APIClient {
    private static final String TAG = "librus-client-log";
    private final Context context;
    private final OkHttpClient client = new OkHttpClient();
    boolean debug = false;

    APIClient(Context _context) {
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
        if (debug) Log.d(TAG, text);
    }

    private Promise<JSONObject, Integer, Void> APIRequest(final String endpoint) {
        final Deferred<JSONObject, Integer, Void> deferred = new DeferredObject<>();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String access_token = preferences.getString("access_token", "");
        String BASE_URL = "https://api.librus.pl/2.0";
        final Request request = new Request.Builder().addHeader("Authorization", "Bearer " + access_token)
                .url(BASE_URL + endpoint)
                .build();
        log("Performing APIRequest " +
                "Endpoint: " + endpoint);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
                deferred.reject(0);
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        deferred.resolve(new JSONObject(response.body().string()));
                        log("API Request " + endpoint + " successful.");
                        response.body().close();
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

    Promise<List<EventCategory>, Void, Void> getEventCategories() {

        final Deferred<List<EventCategory>, Void, Void> deferred = new DeferredObject<>();

        APIRequest("/HomeWorks/Categories").then(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject result) {
                try {
                    List<EventCategory> res = new ArrayList<>();
                    JSONArray rawCategories = result.getJSONArray("Categories");
                    for (int i = 0; i < rawCategories.length(); i++) {
                        JSONObject category = rawCategories.getJSONObject(i);
                        res.add(new EventCategory(category.getString("Id"), category.getString("Name")));
                    }
                    deferred.resolve(res);
                } catch (JSONException e) {
                    e.printStackTrace();
                    deferred.reject(null);
                }
            }
        });

        return deferred.promise();
    }

    Promise<List<Event>, Void, Void> getEvents() {

        final Deferred<List<Event>, Void, Void> deferred = new DeferredObject<>();

        APIRequest("/HomeWorks").done(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject result) {
                try {
                    List<Event> res = new ArrayList<>();
                    JSONArray events = result.getJSONArray("HomeWorks");
                    for (int i = 0; i < events.length(); i++) {
                        JSONObject rawEvent = events.getJSONObject(i);
                        String categoryId = rawEvent.getJSONObject("Category").getString("Id");
                        String description = rawEvent.getString("Content");
                        LocalDate date = LocalDate.parse(rawEvent.getString("Date"));
                        int lessonNumber = Integer.parseInt(rawEvent.getString("LessonNo"));
                        res.add(new Event(categoryId, description, date, lessonNumber));
                    }
                    deferred.resolve(res);
                } catch (JSONException e) {
                    e.printStackTrace();
                    deferred.reject(null);
                }
            }
        });

        return deferred.promise();
    }

    Promise<List<Teacher>, Void, Void> getTeachers() {
        final Deferred<List<Teacher>, Void, Void> deferred = new DeferredObject<>();

        APIRequest("/Users").then(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject result) {
                try {
                    List<Teacher> res = new ArrayList<>();
                    JSONArray rawTeachers = result.getJSONArray("Users");
                    for (int i = 0; i < rawTeachers.length(); i++) {
                        JSONObject rawTeacher = rawTeachers.getJSONObject(i);
                        Teacher teacher = new Teacher(rawTeacher.getString("Id"));
                        teacher.setName(rawTeacher.getString("FirstName"), rawTeacher.getString("LastName"));
                        res.add(teacher);

                    }
                    deferred.resolve(res);
                } catch (JSONException e) {
                    e.printStackTrace();
                    deferred.reject(null);
                }
            }
        });

        return deferred.promise();
    }

    Promise<List<Subject>, Void, Void> getSubjects() {
        final Deferred<List<Subject>, Void, Void> deferred = new DeferredObject<>();

        APIRequest("/Subjects").then(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject result) {
                try {
                    List<Subject> res = new ArrayList<>();
                    JSONArray rawSubjects = result.getJSONArray("Subjects");
                    for (int i = 0; i < rawSubjects.length(); i++) {
                        JSONObject rawSubject = rawSubjects.getJSONObject(i);
                        Subject subject = new Subject(rawSubject.getString("Id"));
                        subject.setName(rawSubject.getString("Name"));
                        res.add(subject);
                    }
                    deferred.resolve(res);
                } catch (JSONException e) {
                    e.printStackTrace();
                    deferred.reject(null);
                }
            }
        });

        return deferred.promise();
    }

    Promise<List<GradeCategory>, Void, Void> getGradeCategories() {
        final Deferred<List<GradeCategory>, Void, Void> deferred = new DeferredObject<>();
        APIRequest("/Grades/Categories").done(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject result) {
                try {
                    List<GradeCategory> res = new ArrayList<>();
                    JSONArray rawGradeCategories = result.getJSONArray("Categories");
                    for (int i = 0; i < rawGradeCategories.length(); i++) {
                        JSONObject rawGradeCategory = rawGradeCategories.getJSONObject(i);
                        int weight = rawGradeCategory.has("Weight") ? rawGradeCategory.getInt("Weight") : 0;
                        res.add(new GradeCategory(
                                rawGradeCategory.getString("Id"),
                                rawGradeCategory.getString("Name"),
                                weight));
                    }
                    deferred.resolve(res);
                } catch (JSONException e) {
                    e.printStackTrace();
                    deferred.reject(null);
                }
            }
        });
        return deferred.promise();
    }

    Promise<List<Announcement>, Void, Void> getAnnouncements() {
        final Deferred<List<Announcement>, Void, Void> deferred = new DeferredObject<>();

        APIRequest("/SchoolNotices").then(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject result) {
                try {
                    List<Announcement> res = new ArrayList<>();
                    JSONArray rawAnnouncements = result.getJSONArray("SchoolNotices");
                    for (int i = 0; i < rawAnnouncements.length(); i++) {
                        JSONObject announcement = rawAnnouncements.getJSONObject(i);
                        res.add(new Announcement(
                                announcement.getString("Id"),
                                LocalDate.parse(announcement.getString("StartDate")),
                                LocalDate.parse(announcement.getString("EndDate")),
                                announcement.getString("Subject"),
                                announcement.getString("Content"),
                                announcement.getJSONObject("AddedBy").getString("Id")));
                    }
                    deferred.resolve(res);
                } catch (JSONException e) {
                    e.printStackTrace();
                    deferred.reject(null);
                }
            }
        });

        return deferred.promise();
    }

    Promise<LibrusAccount, Void, Void> getAccount() {
        final Deferred<LibrusAccount, Void, Void> deferred = new DeferredObject<>();
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
                deferred.reject(null);
            }
        });
        return deferred.promise();
    }

    Promise<LuckyNumber, Void, Void> getLuckyNumber() {
        final Deferred<LuckyNumber, Void, Void> deferred = new DeferredObject<>();
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
                deferred.reject(null);
            }
        });
        return deferred.promise();
    }

    Promise<Timetable, Void, Void> getTimetable(final LocalDate... weeks) {

        final Deferred<Timetable, Void, Void> deferred = new DeferredObject<>();

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
                        Iterator<String> dayIterator = rawData.keys();
                        while (dayIterator.hasNext()) {
                            String key = dayIterator.next();

                            LocalDate date = LocalDate.parse(key);
                            SchoolDay schoolDay = new SchoolDay(date);
                            JSONArray rawSchoolDay = rawData.getJSONArray(key);

                            for (int i = 0; i < rawSchoolDay.length(); i++) {
                                try {
                                    if (rawSchoolDay.getJSONArray(i).length() != 0) {
                                        JSONObject rawLesson = rawSchoolDay.getJSONArray(i).getJSONObject(0);
                                        boolean isCanceled = rawLesson.getBoolean("IsCanceled");
                                        boolean isSubstitutionClass = rawLesson.getBoolean("IsSubstitutionClass");
                                        JSONObject rawSubject = rawLesson.getJSONObject("Subject");
                                        JSONObject rawTeacher = rawLesson.getJSONObject("Teacher");
                                        JSONObject rawOrgSubject = isSubstitutionClass ? rawLesson.getJSONObject("OrgSubject") : null;
                                        JSONObject rawOrgTeacher = isSubstitutionClass ? rawLesson.getJSONObject("OrgTeacher") : null;
                                        Subject subject = new Subject(rawSubject.getString("Id"));
                                        subject.setName(rawSubject.getString("Name"));
                                        Teacher teacher = new Teacher(rawTeacher.getString("Id"));
                                        teacher.setName(rawTeacher.getString("FirstName"), rawTeacher.getString("LastName"));
                                        schoolDay.setLesson(i, new Lesson(
                                                i,
                                                date,
                                                LocalTime.parse(rawLesson.getString("HourFrom"), DateTimeFormat.forPattern("HH:mm")),
                                                LocalTime.parse(rawLesson.getString("HourTo"), DateTimeFormat.forPattern("HH:mm")),
                                                subject,
                                                teacher,
                                                isCanceled,
                                                isSubstitutionClass,
                                                isSubstitutionClass ? new Subject(rawOrgSubject.getString("Id")) : null,
                                                isSubstitutionClass ? new Teacher(rawOrgTeacher.getString("Id")) : null,
                                                null)
                                        );
                                        schoolDay.setEmpty(false);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            timetable.addSchoolDay(schoolDay);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                deferred.resolve(timetable);
            }

        });

        return deferred.promise();
    }

    Promise<List<Grade>, Void, Void> getGrades() {
        final Deferred<List<Grade>, Void, Void> deferred = new DeferredObject<>();
        APIRequest("/Grades").then(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject result) {
                try {
                    List<Grade> res = new ArrayList<>();
                    JSONArray rawGrades = result.getJSONArray("Grades");
                    for (int i = 0; i < rawGrades.length(); i++) {
                        JSONObject rawGrade = rawGrades.getJSONObject(i);
                        Grade.Type type;
                        if (rawGrade.getBoolean("IsSemester"))
                            type = Grade.Type.SEMESTER;
                        else if (rawGrade.getBoolean("IsSemesterProposition"))
                            type = Grade.Type.SEMESTER_PROPOSITION;
                        else if (rawGrade.getBoolean("IsFinal"))
                            type = Grade.Type.FINAL;
                        else if (rawGrade.getBoolean("IsFinalProposition"))
                            type = Grade.Type.FINAL_PROPOSITION;
                        else
                            type = Grade.Type.NORMAL;
                        //TODO add comments
                        res.add(new Grade(
                                rawGrade.getString("Id"),
                                rawGrade.getString("Grade"),
                                rawGrade.getJSONObject("Lesson").getString("Id"),
                                rawGrade.getJSONObject("Subject").getString("Id"),
                                rawGrade.getJSONObject("Category").getString("Id"),
                                rawGrade.getJSONObject("AddedBy").getString("Id"),
                                rawGrade.getInt("Semester"),
                                LocalDate.parse(rawGrade.getString("Date")),
                                LocalDateTime.parse(rawGrade.getString("AddDate"), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")),
                                type
                        ));
                    }
                    deferred.resolve(res);
                } catch (Exception e) {
                    deferred.reject(null);
                    e.printStackTrace();
                }
            }
        });
        return deferred.promise();
    }

    Promise<List<Average>, Void, Void> getAverages() {
        final Deferred<List<Average>, Void, Void> deferred = new DeferredObject<>();
        APIRequest("/Grades/Averages").then(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject result) {
                try {
                    List<Average> res = new ArrayList<>();
                    JSONArray rawAverages = result.getJSONArray("Averages");
                    for (int i = 0; i < rawAverages.length(); i++) {
                        JSONObject rawAverage = rawAverages.getJSONObject(i);
                        res.add(new Average(
                                rawAverage.getJSONObject("Subject").getString("Id"),
                                rawAverage.getDouble("Semester1"),
                                rawAverage.getDouble("Semester2"),
                                rawAverage.getDouble("FullYear")));
                    }
                    deferred.resolve(res);
                } catch (Exception e) {
                    deferred.reject(null);
                    e.printStackTrace();
                }
            }
        });
        return deferred.promise();
    }

    Promise<List<TextGrade>, Void, Void> getTextGrades() {
        final Deferred<List<TextGrade>, Void, Void> deferred = new DeferredObject<>();
        APIRequest("/TextGrades").then(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject result) {
                try {
                    List<TextGrade> res = new ArrayList<>();
                    JSONArray rawTextGrades = result.getJSONArray("Grades");

                    for (int i = 0; i < rawTextGrades.length(); i++) {
                        JSONObject rawTextGrade = rawTextGrades.getJSONObject(i);
                        TextGrade.Type type;
                        if (rawTextGrade.getBoolean("IsSemester"))
                            type = TextGrade.Type.SEMESTER;
                        else if (rawTextGrade.getBoolean("IsSemesterProposition"))
                            type = TextGrade.Type.SEMESTER_PROPOSITION;
                        else if (rawTextGrade.getBoolean("IsFinal"))
                            type = TextGrade.Type.FINAL;
                        else if (rawTextGrade.getBoolean("IsFinalProposition"))
                            type = TextGrade.Type.FINAL_PROPOSITION;
                        else
                            type = TextGrade.Type.NORMAL;
                        res.add(new TextGrade(rawTextGrade.getString("Id"),
                                rawTextGrade.getString("Grade"),
                                rawTextGrade.getJSONObject("Lesson").getString("Id"),
                                rawTextGrade.getJSONObject("Subject").getString("Id"),
                                rawTextGrade.getJSONObject("Category").getString("Id"),
                                rawTextGrade.getJSONObject("AddedBy").getString("Id"),
                                rawTextGrade.getInt("Semester"),
                                LocalDate.parse(rawTextGrade.getString("Date")),
                                LocalDateTime.parse(rawTextGrade.getString("AddDate"), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")),
                                type));
                    }
                    deferred.resolve(res);
                } catch (JSONException e) {
                    deferred.reject(null);
                    e.printStackTrace();
                }
            }
        });
        return deferred.promise();
    }
}
