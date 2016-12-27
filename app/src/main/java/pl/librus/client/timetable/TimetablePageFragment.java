package pl.librus.client.timetable;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import pl.librus.client.R;
import pl.librus.client.api.Lesson;
import pl.librus.client.api.SchoolDay;

/**
 * Created by szyme on 26.12.2016. librus-client
 */

public class TimetablePageFragment extends Fragment {
    private static final String ARG_SCHOOLDAY = "TimetablePageFragment:schoolday";
    private final String TAG = "librus-client-log";

    public static TimetablePageFragment newInstance(SchoolDay d) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_SCHOOLDAY, d);
        TimetablePageFragment fragment = new TimetablePageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SchoolDay schoolDay = (SchoolDay) getArguments().getSerializable(ARG_SCHOOLDAY);
        assert schoolDay != null;
        if (schoolDay.isEmpty())
            return inflater.inflate(R.layout.fragment_timetable_page_empty, container, false);
        View root = inflater.inflate(R.layout.fragment_timetable_page, container, false);
        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.fragment_timetable_page_recycler);

        List<TabLessonItem> items = new ArrayList<>();
        for (Map.Entry<Integer, Lesson> entry : schoolDay.getLessons().entrySet())
            items.add(new TabLessonItem(entry.getValue(), getContext()));
        Collections.sort(items);
        FlexibleAdapter<TabLessonItem> adapter = new FlexibleAdapter<>(items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        return root;
    }
}
