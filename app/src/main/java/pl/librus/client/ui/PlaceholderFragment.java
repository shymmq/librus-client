package pl.librus.client.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.librus.client.R;

public class PlaceholderFragment extends MainFragment {
    public PlaceholderFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_placeholder, container, false);
    }

    @Override
    public void refresh() {

    }

    @Override
    public void setOnSetupCompleteListener(OnSetupCompleteListener listener) {

    }

    @Override
    public void removeListener() {

    }

    @Override
    public int getTitle() {
        return R.string.app_name;
    }
}
