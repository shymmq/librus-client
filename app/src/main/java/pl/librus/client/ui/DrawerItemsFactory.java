package pl.librus.client.ui;

import android.support.v4.app.Fragment;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.List;

import java8.util.function.Consumer;
import java8.util.stream.StreamSupport;
import pl.librus.client.announcements.AnnouncementsFragment;
import pl.librus.client.attendances.AttendanceFragment;
import pl.librus.client.grades.GradesFragment;
import pl.librus.client.timetable.TimetableFragment;


public class DrawerItemsFactory {
    public IDrawerItem[] getItems(Consumer<BaseFragment> displayFragment) {
        Function<BaseFragment, Drawer.OnDrawerItemClickListener> clickListenerConverter = fragment ->
                (view, position, drawerItem) -> {
                    displayFragment.accept(fragment);
                    return false;
                };

        return StreamSupport.stream(new FragmentsRepository().getAll())
                .map(fragment -> new PrimaryDrawerItem()
                        .withIdentifier(fragment.getTitle())
                        .withIconTintingEnabled(true)
                        .withName(fragment.getTitle())
                        .withIcon(fragment.getIcon())
                        .withOnDrawerItemClickListener(clickListenerConverter.apply(fragment)))
                .toArray(IDrawerItem[]::new);
    }

}
