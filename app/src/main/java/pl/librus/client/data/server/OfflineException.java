package pl.librus.client.data.server;

public class OfflineException extends HttpException {
    public OfflineException(String url) {
        super("Server Offline", url);
    }
}
