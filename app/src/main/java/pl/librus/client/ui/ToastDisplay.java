package pl.librus.client.ui;

import android.content.Context;
import android.widget.Toast;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by robwys on 28/03/2017.
 */

@Singleton
public class ToastDisplay {
    private final Context context;

    @Inject
    public ToastDisplay(Context context) {
        this.context = context;
    }

    public void display(String text, int duration) {
        Toast.makeText(context, text, duration).show();
    }
}
