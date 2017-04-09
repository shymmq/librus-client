package pl.librus.client.ui;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.google.common.base.Optional;

import java.util.List;

import pl.librus.client.domain.LuckyNumber;
import pl.librus.client.domain.Me;
import pl.librus.client.presentation.FragmentPresenter;

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
