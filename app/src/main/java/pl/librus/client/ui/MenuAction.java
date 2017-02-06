package pl.librus.client.ui;

/**
 * Created by szyme on 05.02.2017.
 */

public interface MenuAction {
    String getName();

    void run();

    boolean isEnabled();
}
