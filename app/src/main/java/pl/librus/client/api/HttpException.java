package pl.librus.client.api;

/**
 * Created by robwys on 10/02/2017.
 */

public class HttpException extends RuntimeException {
    private final int code;

    public HttpException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
