package pl.librus.client.ui;

import pl.librus.client.R;
import pl.librus.client.presentation.ReloadablePresenter;


public class ReloadAction implements MenuAction {
    private final ReloadablePresenter presenter;

    public ReloadAction(ReloadablePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public int getName() {
        return R.string.refresh;
    }

    @Override
    public void run() {
        presenter.reload();
    }

    @Override
    public boolean isEnabled() {
        return !presenter.isReloading();
    }
}
