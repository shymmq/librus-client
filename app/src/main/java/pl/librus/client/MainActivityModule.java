package pl.librus.client;

import com.google.common.collect.Sets;

import java.util.Set;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;
import pl.librus.client.presentation.AnnouncementsPresenter;
import pl.librus.client.presentation.AttendancesPresenter;
import pl.librus.client.presentation.GradesPresenter;
import pl.librus.client.presentation.MainFragmentPresenter;
import pl.librus.client.presentation.TimetablePresenter;
import pl.librus.client.ui.MainActivity;
import pl.librus.client.ui.MainActivityOps;
import pl.librus.client.ui.NavigationOps;

/**
 * Created by robwys on 28/03/2017.
 */
@Module
public class MainActivityModule {

    private final MainActivity mainActivity;

    public MainActivityModule(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Provides
    @MainActivityScope
    MainActivityOps provideMainActivity() {
        return mainActivity;
    }

    @Provides
    @MainActivityScope
    NavigationOps provideNavigationOps() {
        return mainActivity;
    }

    @Provides
    @ElementsIntoSet
    @MainActivityScope
    Set<MainFragmentPresenter> providePresenters(
            AttendancesPresenter attendancesPresenter,
            AnnouncementsPresenter announcementsPresenter,
            TimetablePresenter timetablePresenter,
            GradesPresenter gradesPresenter) {
        return Sets.newHashSet(
                attendancesPresenter,
                announcementsPresenter,
                timetablePresenter,
                gradesPresenter);
    }

}
