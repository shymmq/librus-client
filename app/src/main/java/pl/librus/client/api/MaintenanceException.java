package pl.librus.client.api;


public class MaintenanceException extends HttpException {

    public MaintenanceException() {
        super("Maintenance");
    }
}
