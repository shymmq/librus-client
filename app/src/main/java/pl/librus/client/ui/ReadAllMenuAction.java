package pl.librus.client.ui;

import android.content.Context;

import java.util.List;

import pl.librus.client.R;
import pl.librus.client.api.Reader;
import pl.librus.client.datamodel.Identifiable;

/**
 * Created by szyme on 05.02.2017.
 */

public class ReadAllMenuAction implements MenuAction {
    private final List<? extends Identifiable> items;
    private final Context context;
    private final Reader reader;

    public ReadAllMenuAction(List<? extends Identifiable> items, Context context) {
        this.items = items;
        this.context = context;
        reader = new Reader(context);

    }

    @Override
    public String getName() {
        return context.getString(R.string.mark_all_as_read);
    }

    @Override
    public void run() {
        for (Identifiable i : items) {
            reader.read(i);
        }
    }

    @Override
    public boolean isEnabled() {
        for (Identifiable object : items) {
            if (!reader.isRead(object)) {
                return true;
            }
        }
        return false;
    }
}
