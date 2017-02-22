package pl.librus.client.ui;

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


public abstract class DefaultDrawerItemsFactory {
    public IDrawerItem[] getItems(Consumer<MainFragment> displayFragment) {
        ListenerFactory listenerFactory = new ListenerFactory(displayFragment);

        return StreamSupport.stream(getFragments())
                .map(fragment -> new PrimaryDrawerItem()
                        .withIconTintingEnabled(true)
                        .withName(fragment.getTitle())
                        .withIcon(fragment.getIcon())
                        .withOnDrawerItemClickListener(listenerFactory.apply(fragment)))
                .toArray(IDrawerItem[]::new);
    }

    public static class ListenerFactory implements Function<MainFragment, Drawer.OnDrawerItemClickListener> {

        private final Consumer<MainFragment> displayFragment;

        public ListenerFactory(Consumer<MainFragment> displayFragment) {
            this.displayFragment = displayFragment;
        }

        @Override
        public Drawer.OnDrawerItemClickListener apply(MainFragment input) {
            return (view, position, drawerItem) -> {
                displayFragment.accept(input);
                return false;
            };
        }
    }

    protected List<MainFragment> getFragments() {
        return Lists.newArrayList(
                new TimetableFragment(),
                new GradesFragment(),
                new AnnouncementsFragment(),
                new AttendanceFragment()
        );
    }
}
