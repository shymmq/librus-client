package pl.librus.client.ui;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class FragmentsRepository extends DefaultFragmentsRepository{

    @Override
    public List<MainFragment> getAll() {
        return ImmutableList.<MainFragment>builder()
                .addAll(super.getAll())
                .add(new NotificationTesterFragment())
                .build();
    }
}
