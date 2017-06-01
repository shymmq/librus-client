package pl.librus.client.presentation;

import com.google.common.collect.Ordering;

import java.util.List;
import java.util.Set;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import pl.librus.client.ui.MainView;

/**
 * Created by robwys on 28/03/2017.
 */

public abstract class MainFragmentPresenter<T extends MainView> extends FragmentPresenter<T> {

    public abstract int getOrder();

    public static List<MainFragmentPresenter> sorted(Set<MainFragmentPresenter> presenters) {
        return StreamSupport.stream(presenters)
                .sorted(Ordering.natural().onResultOf(MainFragmentPresenter::getOrder))
                .collect(Collectors.toList());
    }

}
