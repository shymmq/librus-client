package pl.librus.client.ui;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by szyme on 05.12.2016.
 * Interface for all directly shown fragments
 */

public abstract class MainFragment extends Fragment {

    private Runnable onSetupComplete;

    void runAfterSetup(Runnable r) {
        this.onSetupComplete = r;
    }

    public List<? extends MenuAction> getMenuItems() {
        return new ArrayList<>();
    }

    public interface OnSetupCompleteListener {
        void run();
    }

    @StringRes
    public abstract int getTitle();

    @DrawableRes
    public abstract int getIcon();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (onSetupComplete != null) onSetupComplete.run();
        onSetupComplete = null;
    }
}
