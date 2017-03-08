package pl.librus.client.api;

import android.content.Context;
import android.preference.PreferenceManager;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.reactivex.Single;
import io.requery.Persistable;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import pl.librus.client.LibrusUtils;
import pl.librus.client.datamodel.Identifiable;
import pl.librus.client.datamodel.lesson.Timetable;

import static pl.librus.client.LibrusUtils.log;

class DefaultAPIClient implements IAPIClient {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private final Context context;

    public DefaultAPIClient(Context _context) {
        context = _context;
    }

    public Single<String> login(String username, String password) {
        final String AUTH_URL = "https://api.librus.pl/OAuth/Token";
        final String auth_token = "MzU6NjM2YWI0MThjY2JlODgyYjE5YTMzZjU3N2U5NGNiNGY=";
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .add("grant_type", "password")
                .add("librus_long_term_token", "1")
                .add("librus_rules_accepted", "true")
                .add("librus_mobile_rules_accepted", "true")
                .build();

        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic " + auth_token)
                .url(AUTH_URL)
                .post(formBody)
                .build();

        return new RxHttpClient().executeCall(request)
                .doOnSuccess(this::saveTokens)
                .map(r -> username);
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

    private boolean tokenExpired(Throwable e) {
        return e instanceof HttpException &&
                e.getMessage().contains("Access Token expired");
    }

    private Single<String> APIRequest(String endpoint) {
        return fetchData(endpoint)
                .onErrorResumeNext(cause -> {
                    if (tokenExpired(cause)) {
                        log("Retrying APIRequest " + "Endpoint: " + endpoint);

                        return refreshAccess()
                                .flatMap(o -> fetchData(endpoint));
                    } else {
                        return Single.error(cause);
                    }
                });
    }

    private Single<String> fetchData(final String endpoint) {
        String access_token = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("access_token", "");
        String url = "https://api.librus.pl/2.0" +
                endpoint;

        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + access_token)
                .url(url)
                .get()
                .build();
        return new RxHttpClient().executeCall(request);
    }

    private Single<?> refreshAccess() {
        String refresh_token = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("refresh_token", null);

        log("Refreshing... \n" +
                "Refresh token: " + refresh_token);

        String AUTH_URL = "https://api.librus.pl/OAuth/Token";
        String auth_token = "MzU6NjM2YWI0MThjY2JlODgyYjE5YTMzZjU3N2U5NGNiNGY=";

        RequestBody body = new FormBody.Builder()
                .add("grant_type", "refresh_token")
                .add("refresh_token", refresh_token)
                .build();

        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic " + auth_token)
                .post(body)
                .url(AUTH_URL)
                .build();

        return new RxHttpClient().executeCall(request)
                .doOnSuccess(this::saveTokens);
    }

    Single<?> pushDevices(final String regToken) {
        try {
            String access_token = PreferenceManager.getDefaultSharedPreferences(context)
                    .getString("access_token", "");

            String PUSH_DEVICES_URL = "https://api.librus.pl/2.0/PushDevices";
            JSONObject bodyJSON = new JSONObject();

            bodyJSON.put("provider", "Android_dru");
            bodyJSON.put("device", regToken);

            RequestBody body = RequestBody.create(JSON, bodyJSON.toString());
            Request request = new Request.Builder()
                    .addHeader("Authorization", "Bearer " + access_token)
                    .url(PUSH_DEVICES_URL)
                    .post(body)
                    .build();

            return new RxHttpClient().executeCall(request)
                    .doOnSuccess(response -> LibrusUtils.log("Device registered"));

        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Single<Timetable> getTimetable(final LocalDate weekStart) {

        String endpoint = "/Timetables?weekStart=" + weekStart.toString("yyyy-MM-dd");

        return getList(endpoint, "Timetable", Timetable.class)
                .map(Iterables::getOnlyElement);
    }

    public <T extends Persistable> Single<List<T>> getAll(Class<T> clazz) {
        EntityInfo info = EntityInfos.infoFor(clazz);
        return getList(info.endpoint(), info.topLevelName(), clazz);
    }

    public <T> Single<List<T>> getList(String endpoint, final String topLevelName, final Class<T> clazz) {
        return APIRequest(endpoint)
                .map(s -> EntityParser.parseList(s, topLevelName, clazz));
    }

    @Override
    public <T extends Persistable & Identifiable> Single<T> getById(Class<T> clazz, String id) {
        EntityInfo info = EntityInfos.infoFor(clazz);

        return APIRequest(info.endpoint(id))
                .map(s -> EntityParser.parseObject(s, info.name(), clazz))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toSingle();
    }
}