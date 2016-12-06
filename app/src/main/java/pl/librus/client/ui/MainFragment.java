package pl.librus.client.ui;

import android.support.v4.app.Fragment;

import pl.librus.client.api.LibrusCache;

/**
 * Created by szyme on 05.12.2016.
 */

public abstract class MainFragment extends Fragment {
    public abstract void refresh(LibrusCache cache);
}
