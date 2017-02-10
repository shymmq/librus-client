package pl.librus.client.api;

/**
 * Created by robwys on 10/02/2017.
 */

public class HttpException extends RuntimeException {
    private final int code;
    private final String message;

    public HttpException(int code, String message, Throwable cause) {
        super(cause);
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
