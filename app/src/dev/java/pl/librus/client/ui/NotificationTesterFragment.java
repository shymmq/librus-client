package pl.librus.client.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.requery.Persistable;
import java8.util.function.Consumer;
import pl.librus.client.R;
import pl.librus.client.api.DatabaseStrategy;
import pl.librus.client.api.LibrusData;
import pl.librus.client.api.NotificationService;
import pl.librus.client.datamodel.Event;
import pl.librus.client.datamodel.LuckyNumber;
import pl.librus.client.datamodel.announcement.Announcement;
import pl.librus.client.datamodel.grade.Grade;

public class NotificationTesterFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        NotificationService service = new NotificationService(getContext(), LibrusData.getInstance(getContext()));
        View v = inflater.inflate(R.layout.notification_tester, container, false);

        addListener(
                v.findViewById(R.id.button_grade),
                Grade.class,
                service::addGrades,
                1
        );

        addListener(
                v.findViewById(R.id.button_grades),
                Grade.class,
                service::addGrades,
                10
        );

        addListener(
                v.findViewById(R.id.button_announcement),
                Announcement.class,
                service::addAnnouncements,
                1
        );

        addListener(
                v.findViewById(R.id.button_announcements),
                Announcement.class,
                service::addAnnouncements,
                10
        );

        addListener(
                v.findViewById(R.id.button_event),
                Event.class,
                service::addEvents,
                1
        );

        addListener(
                v.findViewById(R.id.button_events),
                Event.class,
                service::addEvents,
                10
        );

        addListener(
                v.findViewById(R.id.button_lucky),
                LuckyNumber.class,
                service::addLuckyNumber,
                1
        );

        v.findViewById(R.id.snackbar).setOnClickListener(snackView -> Snackbar.make(
                getActivity().findViewById(R.id.activity_main_coordinator),
                R.string.offline_data_error,
                Snackbar.LENGTH_LONG)
                .show());

        return v;
    }

    private <T extends Persistable> void addListener(View button, Class<T> clazz, Consumer<List<T>> method, int count) {
        button.setOnClickListener(v -> method.accept(getMany(clazz, count)));
    }

    private <T extends Persistable> List<T> getMany(Class<T> clazz, int count) {
        return DatabaseStrategy.getInstance(getContext())
                .getAll(clazz)
                .take(count)
                .toList()
                .blockingGet();
    }

    @Override
    public int getTitle() {
        return R.string.notification_tester;
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_notifications_black_24dp;
    }
}
