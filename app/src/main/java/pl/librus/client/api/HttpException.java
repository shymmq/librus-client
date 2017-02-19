package pl.librus.client.api;

public class HttpException extends RuntimeException {
    private final int code;

    public HttpException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
