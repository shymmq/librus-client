package pl.librus.client.presentation;

import android.support.design.widget.Snackbar;

import javax.inject.Inject;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import pl.librus.client.R;
import pl.librus.client.data.server.HttpException;
import pl.librus.client.data.server.MaintenanceException;
import pl.librus.client.data.server.OfflineException;
import pl.librus.client.ui.MainActivityOps;
import pl.librus.client.util.LibrusUtils;

/**
 * Created by robwys on 09/04/2017.
 */

public class ErrorHandler implements Consumer<Throwable> {

    private final MainActivityOps mainActivity;

    @Inject
    public ErrorHandler(MainActivityOps mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void accept(@NonNull Throwable exception) throws Exception {
        handler(() -> {}).accept(exception);
    }

    public Consumer<Throwable> handler(Runnable normalExecution) {
        return exception -> {
            LibrusUtils.log("Handle update error");
            if (exception instanceof OfflineException || exception instanceof MaintenanceException) {
                LibrusUtils.log("Offline mode");
                mainActivity.showSnackBar(R.string.offline_data_error, Snackbar.LENGTH_LONG);
                normalExecution.run();
            } else {
                LibrusUtils.logError("Unknown error");
                exception.printStackTrace();
                mainActivity.showSnackBar(R.string.unknown_error, Snackbar.LENGTH_LONG);
            }
        };
    }
}
