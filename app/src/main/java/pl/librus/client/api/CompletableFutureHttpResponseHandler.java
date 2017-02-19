package pl.librus.client.api;

import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import java8.util.concurrent.CompletableFuture;

public class CompletableFutureHttpResponseHandler extends TextHttpResponseHandler {

    private final CompletableFuture<String> future = new CompletableFuture<>();

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        future.completeExceptionally(new HttpException(statusCode, responseString, throwable));
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseString) {
        System.out.println(android.os.Process.myTid());
        future.complete(responseString);
    }

    public CompletableFuture<String> getFuture() {
        return future;
    }
}
