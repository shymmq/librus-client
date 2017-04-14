package pl.librus.client.data.server;


public class MaintenanceException extends HttpException {

    public MaintenanceException(String url) {
        super("Maintenance", url);
    }
}
