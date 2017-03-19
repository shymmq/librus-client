package pl.librus.client.api;

public class OfflineException extends HttpException {
    public OfflineException(String url) {
        super("Server Offline", url);
    }
}
