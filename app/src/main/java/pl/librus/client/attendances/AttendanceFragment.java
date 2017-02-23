package pl.librus.client.attendances;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;
import io.requery.Persistable;
import io.requery.sql.EntityDataStore;
import pl.librus.client.R;
import pl.librus.client.datamodel.Attendance;
import pl.librus.client.datamodel.AttendanceCategory;
import pl.librus.client.datamodel.PlainLesson;
import pl.librus.client.datamodel.Subject;
import pl.librus.client.datamodel.Teacher;
import pl.librus.client.ui.MainApplication;
import pl.librus.client.ui.MainFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttendanceFragment extends MainFragment implements FlexibleAdapter.OnItemClickListener {

    private FlexibleAdapter<IFlexible> adapter;

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
        View root = inflater.inflate(R.layout.fragment_attendances, container, false);
        //Setup RecyclerView
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.fragment_attendances_main_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new FlexibleAdapter<>(null, this);
        recyclerView.setAdapter(adapter);

        EntityDataStore<Persistable> data = MainApplication.getData();

        List<Attendance> attendances = data.select(Attendance.class).get().toList();
        Map<LocalDate, AttendanceHeaderItem> headerItemMap = new HashMap<>();
        for (Attendance attendance : attendances) {
            LocalDate date = attendance.date();

            AttendanceCategory category = MainApplication.getData()
                    .findByKey(AttendanceCategory.class, attendance.type());

            if (!category.presenceKind()) {
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
        }
        List<AttendanceHeaderItem> headers = new ArrayList<>(headerItemMap.values());
        Collections.sort(headers);
        for (AttendanceHeaderItem headerItem : headers) {
            adapter.addSection(headerItem);
        }
        return root;
    }

    @Override
    public int getTitle() {
        return R.string.attendances_view_title;
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_person_outline_black_48dp;
    }

    @Override
    public boolean onItemClick(int position) {
        IFlexible item = adapter.getItem(position);
        if (!(item instanceof AttendanceItem)) return true;
        AttendanceItem attendanceItem = (AttendanceItem) item;
        Attendance attendance = attendanceItem.getAttendance();
        AttendanceCategory category = attendanceItem.getCategory();

        View root = LayoutInflater.from(getContext()).inflate(R.layout.attendance_details, null);

        View subjectContainer = root.findViewById(R.id.attendance_details_subject_container);
        View addedByContainer = root.findViewById(R.id.attendance_details_added_by_container);

        TextView subjectValue = (TextView) root.findViewById(R.id.attendance_details_subject_value);
        TextView dateValue = (TextView) root.findViewById(R.id.attendance_details_date_value);
        TextView addedByValue = (TextView) root.findViewById(R.id.attendance_details_added_by_value);

        EntityDataStore<Persistable> data = MainApplication.getData();

        PlainLesson lesson = data.findByKey(PlainLesson.class, attendance.lesson());
        if (lesson != null) {
            Subject subject = data.findByKey(Subject.class, lesson.subject());
            if (subject != null) {
                addedByContainer.setVisibility(View.VISIBLE);
                subjectValue.setText(subject.name());
            } else {
                subjectContainer.setVisibility(View.GONE);
            }
        } else {
            subjectContainer.setVisibility(View.GONE);
        }

        dateValue.setText(new StringBuilder()
                .append(attendance.date().toString(getContext().getString(R.string.date_format_no_year), new Locale("pl")))
                .append(", ")
                .append(String.valueOf(attendance.lessonNumber()))
                .append(". lekcja"));

        Teacher addedBy = data.findByKey(Teacher.class, attendance.addedBy());
        if (addedBy != null) {
            addedByContainer.setVisibility(View.VISIBLE);
            addedByValue.setText(addedBy.name());
        } else {
            addedByContainer.setVisibility(View.GONE);
        }

        new MaterialDialog.Builder(getActivity())
                .customView(root, true)
                .title(category.name())
                .show();

        return true;
    }
}
