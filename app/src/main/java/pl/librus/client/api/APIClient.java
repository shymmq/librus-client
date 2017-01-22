package pl.librus.client.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
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
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static pl.librus.client.LibrusUtils.log;

public class APIClient {
    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "librus-client-log";
    private final Context context;
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(9, java.util.concurrent.TimeUnit.DAYS)
            .writeTimeout(9, TimeUnit.DAYS)
            .readTimeout(9, TimeUnit.DAYS)
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
                    log("API Request failed\n" +
                                    "Edpoint: " + endpoint + "\n" +
                                    "Access_token: " + access_token + "\n" +
                                    "Response code: " + response.code() + " " + response.message() + "\n" +
                                    "Response: " + response.body().string(),
                            Log.ERROR);
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
                                    log("Second attempt failed. Code " + result,
                                            Log.ERROR);

                                    deferred.reject(result);
                                }
                            });
                        }
                    }).fail(new FailCallback<Response>() {
                        @Override
                        public void onFail(Response result) {

                            //refresh failed
                            log("Refresh failed \n" +
                                            "Response code: " + result + " " + response.message(),
                                    Log.ERROR);

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
                    log("Refresh token request failed: \n" +
                                    "code: " + response.code() + "\n" +
                                    "response: " + response.body().string(),
                            Log.ERROR);
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
                        log("Refresh token request failed: \n" +
                                        "code: " + response.code() + "\n" +
                                        "response: " + response.body().string(),
                                Log.ERROR);
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
                    JSONArray rawEvents = result.getJSONArray("HomeWorks");
                    for (int i = 0; i < rawEvents.length(); i++) {
                        JSONObject rawEvent = rawEvents.getJSONObject(i);
                        String categoryId = rawEvent.getJSONObject("Category").getString("Id");
                        String description = rawEvent.getString("Content");
                        LocalDate date = LocalDate.parse(rawEvent.getString("Date"));
                        String addedById = rawEvent.getJSONObject("CreatedBy").getString("Id");
                        int lessonNumber = rawEvent.isNull("LessonNo") ? -1 : Integer.parseInt(rawEvent.getString("LessonNo"));
                        String id = rawEvent.getString("Id");
                        res.add(new Event(id, categoryId, description, date, addedById, lessonNumber));
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

    public Promise<ArrayList<Teacher>, Void, Void> getTeachers() {
        final Deferred<ArrayList<Teacher>, Void, Void> deferred = new DeferredObject<>();

        APIRequest("/Users").then(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject result) {
                try {
                    ArrayList<Teacher> res = new ArrayList<>();
                    JSONArray rawTeachers = result.getJSONArray("Users");
                    for (int i = 0; i < rawTeachers.length(); i++) {
                        JSONObject rawTeacher = rawTeachers.getJSONObject(i);
                        Teacher teacher = new Teacher(rawTeacher.getString("Id"), rawTeacher.getString("FirstName"), rawTeacher.getString("LastName"));
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

    public Promise<ArrayList<Subject>, Void, Void> getSubjects() {
        final Deferred<ArrayList<Subject>, Void, Void> deferred = new DeferredObject<>();

        APIRequest("/Subjects").then(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject result) {
                try {
                    ArrayList<Subject> res = new ArrayList<>();
                    JSONArray rawSubjects = result.getJSONArray("Subjects");
                    for (int i = 0; i < rawSubjects.length(); i++) {
                        JSONObject rawSubject = rawSubjects.getJSONObject(i);
                        Subject subject = new Subject(rawSubject.getString("Name"), rawSubject.getString("Id"));
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

    Promise<List<PlainLesson>, Void, Void> getPlainLessons() {
        final Deferred<List<PlainLesson>, Void, Void> deferred = new DeferredObject<>();

        APIRequest("/Lessons").then(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject result) {
                try {
                    List<PlainLesson> res = new ArrayList<>();
                    JSONArray rawPlainLessons = result.getJSONArray("Lessons");
                    for (int i = 0; i < rawPlainLessons.length(); i++) {
                        JSONObject rawLesson = rawPlainLessons.getJSONObject(i);
                        JSONObject rawTeacher = rawLesson.getJSONObject("Teacher");
                        JSONObject rawSubject = rawLesson.getJSONObject("Subject");
                        res.add(new PlainLesson(
                                rawLesson.getString("Id"),
                                rawTeacher.getString("Id"),
                                rawSubject.getString("Id")));
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

    public Promise<ArrayList<GradeCategory>, Void, Void> getGradeCategories() {
        final Deferred<ArrayList<GradeCategory>, Void, Void> deferred = new DeferredObject<>();
        APIRequest("/Grades/Categories").done(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject result) {
                try {
                    ArrayList<GradeCategory> res = new ArrayList<>();
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

    public Promise<LibrusAccount, Void, Void> getAccount() {
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

    public Promise<SchoolWeek, Void, Void> getSchoolWeek(final LocalDate weekStart) {

        final Deferred<SchoolWeek, Void, Void> deferred = new DeferredObject<>();

        String endpoint = "/Timetables?weekStart=" + weekStart.toString("yyyy-MM-dd");
        APIRequest(endpoint).then(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject result) {
                final SchoolWeek schoolWeek = new SchoolWeek(weekStart);
                try {
                    JSONObject rawData = result.getJSONObject("Timetable");
                    Iterator<String> dayIterator = rawData.keys();
                    while (dayIterator.hasNext()) {
                        String key = dayIterator.next();

                        LocalDate date = LocalDate.parse(key);
                        SchoolDay schoolDay = new SchoolDay(date);
                        JSONArray rawSchoolDay = rawData.getJSONArray(key);

                        for (int i = 0; i < rawSchoolDay.length(); i++) {
                            if (rawSchoolDay.getJSONArray(i).length() != 0) {
                                JSONObject rawLesson = rawSchoolDay.getJSONArray(i).getJSONObject(0);
                                String id = rawLesson.getJSONObject("Lesson").getString("Id") + date.toString();
                                boolean isCanceled = rawLesson.getBoolean("IsCanceled");
                                boolean isSubstitutionClass = rawLesson.getBoolean("IsSubstitutionClass");
                                int lessonNumber = rawLesson.getInt("LessonNo");
                                JSONObject rawSubject = rawLesson.getJSONObject("Subject");
                                JSONObject rawTeacher = rawLesson.getJSONObject("Teacher");
                                Subject subject = new Subject(rawSubject.getString("Name"), rawSubject.getString("Id"));
                                Teacher teacher = new Teacher(rawTeacher.getString("Id"), rawTeacher.getString("FirstName"), rawTeacher.getString("LastName"));
                                LocalTime hourFrom = LocalTime.parse(rawLesson.getString("HourFrom"), DateTimeFormat.forPattern("HH:mm"));
                                LocalTime hourTo = LocalTime.parse(rawLesson.getString("HourTo"), DateTimeFormat.forPattern("HH:mm"));
                                if (isCanceled && isSubstitutionClass) {
                                    //lesson moved
                                    String newTeacherId = rawLesson.getJSONObject("NewTeacher").getString("Id");
                                    String newSubjectId = rawLesson.getJSONObject("NewSubject").getString("Id");
                                    LocalDate newDate = LocalDate.parse(rawLesson.getString("NewDate"));
                                    int newLessonNo = rawLesson.getInt("NewLessonNo");
                                    schoolDay.setLesson(lessonNumber,
                                            new Lesson(id, lessonNumber, date, hourFrom, hourTo, subject, teacher, newSubjectId, newTeacherId, newLessonNo, newDate));
                                } else if (isCanceled) {
                                    schoolDay.setLesson(lessonNumber,
                                            new Lesson(id, lessonNumber, date, hourFrom, hourTo, subject, teacher, true));
                                } else if (isSubstitutionClass) {
                                    String orgSubjectId = rawLesson.getJSONObject("OrgSubject").getString("Id");
                                    String orgTeacherId = rawLesson.getJSONObject("OrgTeacher").getString("Id");
                                    schoolDay.setLesson(lessonNumber,
                                            new Lesson(id, lessonNumber, date, hourFrom, hourTo, subject, teacher, orgSubjectId, orgTeacherId));
                                } else {
                                    schoolDay.setLesson(lessonNumber,
                                            new Lesson(id, lessonNumber, date, hourFrom, hourTo, subject, teacher));
                                }
                                schoolDay.setEmpty(false);
                            }
                        }
                        schoolWeek.addSchoolDay(schoolDay);
                    }
                    deferred.resolve(schoolWeek);
                } catch (JSONException e) {
                    log(result.toString(), Log.ERROR, false);
                    e.printStackTrace();
                    deferred.reject(null);
                }
            }
        });

        return deferred.promise();
    }

    public Promise<List<Grade>, Void, Void> getGrades() {
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
                        res.add(new Grade(
                                rawGrade.getString("Id"),
                                rawGrade.getString("Grade"),
                                rawGrade.getJSONObject("Lesson").getString("Id"),
                                rawGrade.getJSONObject("Subject").getString("Id"),
                                rawGrade.getJSONObject("Category").getString("Id"),
                                rawGrade.getJSONObject("AddedBy").getString("Id"),
                                rawGrade.has("Comments") ? rawGrade.getJSONArray("Comments").getJSONObject(0).getString("Id") : null,
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

    public Promise<List<Average>, Void, Void> getAverages() {
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

    public Promise<List<TextGrade>, Void, Void> getTextGrades() {
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

    public Promise<List<GradeComment>, Void, Void> getComments() {
        final Deferred<List<GradeComment>, Void, Void> deferred = new DeferredObject<>();
        APIRequest("/Grades/Comments").done(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject result) {
                try {
                    List<GradeComment> res = new ArrayList<>();
                    JSONArray rawComments = result.getJSONArray("Comments");
                    for (int i = 0; i < rawComments.length(); i++) {
                        JSONObject rawComment = rawComments.getJSONObject(i);
                        res.add(new GradeComment(rawComment.getString("Id"),
                                rawComment.getJSONObject("AddedBy").getString("Id"),
                                rawComment.getJSONObject("Grade").getString("Id"),
                                rawComment.getString("Text")));
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

    Promise<List<Attendance>, Void, Void> getAttendances() {
        final Deferred<List<Attendance>, Void, Void> deferred = new DeferredObject<>();

        APIRequest("/Attendances").then(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject result) {
                List<Attendance> res = new ArrayList<>();
                try {
                    JSONArray rawAttendances = result.getJSONArray("Attendances");
                    for (int i = 0; i < rawAttendances.length(); i++) {
                        JSONObject attendance = rawAttendances.getJSONObject(i);
                        res.add(new Attendance(
                                attendance.getString("Id"),
                                attendance.getJSONObject("Lesson").getString("Id"),
                                LocalDate.parse(attendance.getString("Date"), DateTimeFormat.forPattern("yyyy-MM-dd")),
                                LocalDateTime.parse(attendance.getString("AddDate"), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")),
                                attendance.getInt("LessonNo"),
                                attendance.getInt("Semester"),
                                attendance.getJSONObject("Type").getString("Id"),
                                attendance.getJSONObject("AddedBy").getString("Id")));
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

    Promise<List<AttendanceCategory>, Void, Void> getAttendanceCategories() {
        final Deferred<List<AttendanceCategory>, Void, Void> deferred = new DeferredObject<>();

        APIRequest("/Attendances/Types").then(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject result) {
                try {
                    List<AttendanceCategory> res = new ArrayList<>();
                    JSONArray rawAttendanceCategories = result.getJSONArray("Types");
                    for (int i = 0; i < rawAttendanceCategories.length(); i++) {
                        JSONObject attendanceCategory = rawAttendanceCategories.getJSONObject(i);
                        res.add(new AttendanceCategory(
                                String.valueOf(attendanceCategory.getInt("Id")),
                                attendanceCategory.getString("Name"),
                                attendanceCategory.getString("Short"),
                                attendanceCategory.getBoolean("Standard"),
                                attendanceCategory.getString("ColorRGB"),
                                attendanceCategory.getBoolean("IsPresenceKind"),
                                attendanceCategory.getInt("Order")));
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
}