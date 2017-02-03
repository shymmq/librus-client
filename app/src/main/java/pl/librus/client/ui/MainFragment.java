package pl.librus.client.ui;

/**
 * Created by szyme on 05.12.2016.
 * Interface for all directly shown fragments
 */

public interface MainFragment {
    void refresh();

    void setOnSetupCompleteLister(OnSetupCompleteListener listener);

    void removeListener();

    interface OnSetupCompleteListener {
        void run();
    }
}
