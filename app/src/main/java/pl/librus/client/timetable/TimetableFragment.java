package pl.librus.client.timetable;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.librus.client.R;
import pl.librus.client.api.LibrusData;
import pl.librus.client.ui.MainFragment;

public class TimetableFragment extends MainFragment {
    private static final String ARG_DATA = "data";
    private final String TAG = "librus-client-log";
    private TabLayout tabLayout;
    private boolean displayDates, useRelativeTabNames;

    public static TimetableFragment newInstance(LibrusData data) {
        TimetableFragment fragment = new TimetableFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATA, data);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_timetable, container, false);

        LibrusData data = (LibrusData) getArguments().getSerializable("data");
        assert data != null;

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.fragment_timetable_recycler);
        LessonAdapter adapter = new LessonAdapter(data);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        return root;
    }

    @Override
    public void refresh(LibrusData cache) {
        Log.d(TAG, "TimetableFragment update()");
    }
}
