package pl.librus.client.timetable;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;

import org.joda.time.LocalDate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import pl.librus.client.R;
import pl.librus.client.api.Lesson;
import pl.librus.client.sql.LibrusDbHelper;

/**
 * Created by szyme on 26.12.2016. librus-client
 */

public class TimetablePageFragment extends Fragment {
    private static final String ARG_DATE = "TimetablePageFragment:date";
    private final String TAG = "librus-client-log";

    public static TimetablePageFragment newInstance(LocalDate date) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        TimetablePageFragment fragment = new TimetablePageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LocalDate date = (LocalDate) getArguments().getSerializable(ARG_DATE);
        View root = inflater.inflate(R.layout.fragment_timetable_page, container, false);
        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.fragment_timetable_page_recycler);
        final TextView message = (TextView) root.findViewById(R.id.fragment_timetable_page_message);
        List<TabLessonItem> items = new ArrayList<>();
        LibrusDbHelper dbHelper = new LibrusDbHelper(getContext());
        try {
            Dao<Lesson, ?> dao = dbHelper.getDao(Lesson.class);
            List<Lesson> lessons = dao.queryForEq(Lesson.COLUMN_NAME_DATE, date);
            if (lessons == null) {
                recyclerView.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
            } else {
                message.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                for (Lesson lesson : lessons)
                    items.add(new TabLessonItem(lesson, getContext()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        FlexibleAdapter<TabLessonItem> adapter = new FlexibleAdapter<>(items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        return root;
    }
}
