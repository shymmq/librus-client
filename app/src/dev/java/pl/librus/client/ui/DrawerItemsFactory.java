package pl.librus.client.ui;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class DrawerItemsFactory extends DefaultDrawerItemsFactory{

    @Override
    protected List<MainFragment> getFragments() {
        return ImmutableList.<MainFragment>builder()
                .addAll(super.getFragments())
                .add(new NotificationTesterFragment())
                .build();
    }
}
