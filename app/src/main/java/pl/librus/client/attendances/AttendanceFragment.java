package pl.librus.client.attendances;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.LocalDate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import pl.librus.client.R;
import pl.librus.client.api.Attendance;
import pl.librus.client.api.LibrusData;
import pl.librus.client.sql.LibrusDbHelper;
import pl.librus.client.ui.MainFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttendanceFragment extends Fragment implements MainFragment {

    public AttendanceFragment() {
        // Required empty public constructor
    }

    public static AttendanceFragment newInstance() {
        return new AttendanceFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FlexibleAdapter.enableLogs(true);

        View root = inflater.inflate(R.layout.fragment_attendance, container, false);
        //Setup RecyclerView
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.fragment_attendances_main_list);

        FlexibleAdapter<AbstractFlexibleItem> adapter = new FlexibleAdapter<>(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        LibrusDbHelper dbHelper = new LibrusDbHelper(getContext());
        List<Attendance> attendances = dbHelper.getAttendances();
        Collections.sort(attendances);
        Map<LocalDate, AttendanceHeaderItem> headerItemMap = new HashMap<>();
        for (Attendance attendance : attendances) {
            LocalDate date = attendance.getDate();
            if (headerItemMap.get(date) == null) {
                headerItemMap.put(date, new AttendanceHeaderItem(date));
            }
            headerItemMap.get(date)
                    .addSubItem(new AttendanceItem(
                            headerItemMap.get(date),
                            attendance));
        }
        for (Map.Entry<LocalDate, AttendanceHeaderItem> entry : headerItemMap.entrySet()) {
            AttendanceHeaderItem headerItem = entry.getValue();
            adapter.addSection(headerItem);
        }
        return root;
    }

    @Override
    public void refresh(LibrusData cache) {

    }

}
