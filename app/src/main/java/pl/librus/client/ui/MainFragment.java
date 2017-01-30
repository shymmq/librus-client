package pl.librus.client.ui;

/**
 * Created by szyme on 05.12.2016.
 */

public interface MainFragment {
    void refresh();

    interface OnSetupCompleteListener {
        public void run();
    }

    void setOnSetupCompleteLister(OnSetupCompleteListener listener);

    void removeListener();
}
