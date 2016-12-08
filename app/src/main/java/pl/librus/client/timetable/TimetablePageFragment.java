package pl.librus.client.timetable;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.Map;

import pl.librus.client.R;
import pl.librus.client.api.EventCategory;
import pl.librus.client.api.SchoolDay;

public class TimetablePageFragment extends Fragment {
    private static final String ARG_DATA = "data";
    private static final String ARG_MAP = "map";
    private final String TAG = "librus-client-log";

    public TimetablePageFragment() {
    }

    public static TimetablePageFragment newInstance(SchoolDay data, Map<String, EventCategory> eventCategoryMap) {
        TimetablePageFragment fragment = new TimetablePageFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATA, data);
        args.putSerializable(ARG_MAP, (Serializable) eventCategoryMap);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        SchoolDay schoolDay = (SchoolDay) getArguments().getSerializable(ARG_DATA);
        Map<String, EventCategory> eventCategoryMap = (Map<String, EventCategory>) getArguments().getSerializable(ARG_MAP);
        if (schoolDay == null) {
            Log.d(TAG, "onCreateView: schoolday == null");
            return inflater.inflate(R.layout.fragment_timetable_page_empty, container, false);
        } else if (schoolDay.isEmpty()) {
            Log.d(TAG, schoolDay.getDate().toString() + " is empty");
            return inflater.inflate(R.layout.fragment_timetable_page_empty, container, false);
        } else {
            View rootView = inflater.inflate(R.layout.fragment_timetable_page, container, false);
            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            RecyclerView.Adapter adapter = new LessonAdapter(schoolDay, eventCategoryMap);
            recyclerView.setAdapter(adapter);
            return rootView;
        }
    }
}