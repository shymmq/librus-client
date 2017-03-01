package pl.librus.client.api;

import java.io.IOException;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pl.librus.client.LibrusUtils;

/**
 * Created by robwys on 10/02/2017.
 */

public class RxHttpClient {

    public Single<String> executeCall(Request request) {
        return Single.<String>create(observer -> {
            try {
                LibrusUtils.log("start fetching data");
                Response response = new OkHttpClient().newCall(request).execute();
                String message = response.body().string();
                if(response.isSuccessful()) {
                    LibrusUtils.log("data fetched succesfully");
                    observer.onSuccess(message);
                } else {
                    observer.onError(new HttpException(response.code(), message));
                }
            } catch (Exception e) {
                observer.onError(e);
            }
        }).subscribeOn(Schedulers.io());
    }
}
