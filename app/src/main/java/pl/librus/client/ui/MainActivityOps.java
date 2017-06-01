package pl.librus.client.ui;

import java.util.List;

/**
 * Created by robwys on 28/03/2017.
 */

public interface MainActivityOps {

    void displayMenuActions(List<? extends MenuAction> actions);

    void navigateToLogin();

    ProgressReporter displayProgressDialog();

    void hideProgressDialog();

    void showSnackBar(int message, int duration);

    void setBackArrow(boolean enable);

    void finish();

    void updateMenu();

    void unregisterGCM();
}
