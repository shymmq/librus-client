package pl.librus.client.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.librus.client.R;


public class LoadingFragment extends MainFragment {
    public static LoadingFragment newInstance() {

        Bundle args = new Bundle();

        LoadingFragment fragment = new LoadingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_loading, container, false);
    }

    @Override
    public void setOnSetupCompleteListener(OnSetupCompleteListener listener) {

    }

    @Override
    public void removeListener() {

    }
}
