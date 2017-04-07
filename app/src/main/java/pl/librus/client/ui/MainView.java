package pl.librus.client.ui;

/**
 * Created by szyme on 04.04.2017.
 */

public interface MainView<T> extends View {

    void display(T content);

    void setRefreshing(boolean b);
}
