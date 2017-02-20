package pl.librus.client.api;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.concurrent.atomic.AtomicInteger;

import java8.util.function.Consumer;
import pl.librus.client.LibrusUtils;

/**
 * Created by robwys on 10/02/2017.
 */

public class ProgressReporter implements Runnable {
    private final Consumer<Integer> callback;
    int total = 0;

    AtomicInteger done = new AtomicInteger(0);
    private final int max;


    public ProgressReporter(int max, Consumer<Integer> callback) {
        this.max = max;
        this.callback = callback;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public void run() {
        callback.accept(max * done.incrementAndGet() / total);
    }
}
