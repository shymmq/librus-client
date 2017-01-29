package pl.librus.client.attendances;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import pl.librus.client.R;
import pl.librus.client.api.Attendance;
import pl.librus.client.api.AttendanceCategory;
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
        View root = inflater.inflate(R.layout.fragment_attendance, container, false);
        //Setup RecyclerView
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.fragment_attendances_main_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FlexibleAdapter<AbstractFlexibleItem> adapter = new FlexibleAdapter<>(null);
        adapter.expandItemsAtStartUp();
        recyclerView.setAdapter(adapter);

        LibrusDbHelper dbHelper = new LibrusDbHelper(getContext());
        List<Attendance> attendances = dbHelper.getAttendances();
        Collections.sort(attendances);
        Map<LocalDate, AttendanceHeaderItem> headerItemMap = new HashMap<>();
        for (Attendance attendance : attendances) {
            LocalDate date = attendance.getDate();

            AttendanceCategory category = dbHelper.getAttendanceCategory(attendance.getTypeId());

            if (headerItemMap.get(date) == null) {
                headerItemMap.put(date, new AttendanceHeaderItem(date));
            }
            AttendanceItem subItem = new AttendanceItem(
                    headerItemMap.get(date),
                    attendance,
                    category);
            headerItemMap.get(date)
                    .addSubItem(subItem);

        }
        List<AttendanceHeaderItem> headers = new ArrayList<>(headerItemMap.values());
        Collections.sort(headers);
        for (AttendanceHeaderItem headerItem : headers) {
            adapter.expand(adapter.addSection(headerItem));
        }
        return root;
    }

    @Override
    public void refresh(LibrusData cache) {

    }

}
