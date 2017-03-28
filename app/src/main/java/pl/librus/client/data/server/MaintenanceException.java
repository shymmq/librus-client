package pl.librus.client.data.server;


public class MaintenanceException extends HttpException {

    public MaintenanceException() {
        super("Maintenance");
    }
}
