package pl.librus.client.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import pl.librus.client.MainActivityComponent;
import pl.librus.client.R;
import pl.librus.client.domain.LuckyNumber;
import pl.librus.client.domain.announcement.Announcement;
import pl.librus.client.domain.event.Event;
import pl.librus.client.domain.grade.Grade;
import pl.librus.client.presentation.MainFragmentPresenter;
import pl.librus.client.presentation.NotificationTesterPresenter;

public class NotificationTesterFragment extends MainFragment implements NotificationTesterView {

    @Inject
    NotificationTesterPresenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mv = inflater.inflate(R.layout.notification_tester, container, false);

        mv.findViewById(R.id.button_grade).setOnClickListener(v -> presenter.sendNotificationClicked(Grade.class, 1));
        mv.findViewById(R.id.button_grades).setOnClickListener(v -> presenter.sendNotificationClicked(Grade.class, 10));

        mv.findViewById(R.id.button_announcement).setOnClickListener(v -> presenter.sendNotificationClicked(Announcement.class, 1));
        mv.findViewById(R.id.button_announcements).setOnClickListener(v -> presenter.sendNotificationClicked(Announcement.class, 10));

        mv.findViewById(R.id.button_event).setOnClickListener(v -> presenter.sendNotificationClicked(Event.class, 1));
        mv.findViewById(R.id.button_events).setOnClickListener(v -> presenter.sendNotificationClicked(Event.class, 10));

        mv.findViewById(R.id.button_lucky).setOnClickListener(v -> presenter.sendNotificationClicked(LuckyNumber.class, 1));

        mv.findViewById(R.id.snackbar).setOnClickListener(snackView -> Snackbar.make(
                getActivity().findViewById(R.id.activity_main_coordinator),
                R.string.offline_data_error,
                Snackbar.LENGTH_LONG)
                .show());
        return mv;
    }

    @Override
    protected void injectPresenter(MainActivityComponent mainActivityComponent) {
        mainActivityComponent.inject(this);
        presenter.attachView(this);
    }

    @Override
    protected MainFragmentPresenter getPresenter() {
        return presenter;
    }

    public void setPresenter(NotificationTesterPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void display(Object content) {
        //Do nothing
    }

    @Override
    public void setRefreshing(boolean b) {
        //Do nothing
    }
}
