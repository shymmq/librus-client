package pl.librus.client.api;

public class ParseException extends RuntimeException {

    public ParseException(String input, Throwable cause) {
        super("input: " + input, cause);
    }

    public ParseException(String input, String message) {
        super(String.format("input: %s, message: %s", input, message));
    }
}
