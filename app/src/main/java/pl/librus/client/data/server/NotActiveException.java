package pl.librus.client.data.server;

/**
 * Created by robwys on 05/06/2017.
 */

public class NotActiveException extends HttpException {

    public NotActiveException() {
        super("is not active");
    }
}
