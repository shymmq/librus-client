package pl.librus.client.ui;

import android.support.v4.app.Fragment;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class FragmentsRepository extends DefaultFragmentsRepository{

    @Override
    public List<BaseFragment> getAll() {
        return ImmutableList.<BaseFragment>builder()
                .addAll(super.getAll())
                .add(new NotificationTesterFragment())
                .build();
    }
}
