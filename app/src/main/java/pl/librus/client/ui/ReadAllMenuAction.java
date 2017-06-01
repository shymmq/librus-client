package pl.librus.client.ui;

import android.content.Context;

import java.util.List;

import pl.librus.client.R;
import pl.librus.client.data.Reader;
import pl.librus.client.domain.Identifiable;
import pl.librus.client.presentation.ReloadablePresenter;

/**
 * Created by szyme on 05.02.2017.
 */

public class ReadAllMenuAction implements MenuAction {
    private final List<? extends Identifiable> items;
    private final Reader reader;
    private final ReloadablePresenter presenter;

    public ReadAllMenuAction(List<? extends Identifiable> items, Context context, ReloadablePresenter presenter) {
        this.items = items;
        reader = new Reader(context);

        this.presenter = presenter;
    }

    @Override
    public int getName() {
        return R.string.mark_all_as_read;
    }

    @Override
    public void run() {
        for (Identifiable i : items) {
            reader.read(i);
        }
        presenter.refresh();
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
