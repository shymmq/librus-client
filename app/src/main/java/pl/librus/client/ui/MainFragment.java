package pl.librus.client.ui;

import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by szyme on 05.12.2016.
 * Interface for all directly shown fragments
 */

public abstract class MainFragment extends Fragment {

    void refresh() {
    }

    public abstract void setOnSetupCompleteListener(OnSetupCompleteListener listener);

    public abstract void removeListener();

    public List<? extends MenuAction> getMenuItems() {
        return new ArrayList<>();
    }

    public interface OnSetupCompleteListener {
        void run();
    }
}
