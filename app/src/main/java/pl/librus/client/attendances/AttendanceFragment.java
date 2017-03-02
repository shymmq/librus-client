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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import pl.librus.client.R;
import pl.librus.client.api.LibrusData;
import pl.librus.client.datamodel.AttendanceCategory;
import pl.librus.client.datamodel.AttendanceWithCategory;
import pl.librus.client.datamodel.FullAttendance;
import pl.librus.client.ui.MainFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttendanceFragment extends MainFragment implements FlexibleAdapter.OnItemClickListener {

    private FlexibleAdapter<IFlexible> adapter;
    private View root;

    public AttendanceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_attendances, container, false);

        LibrusData.findAttendancesWithCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::displayList);

        return root;
    }

    private void displayList(List<? extends AttendanceWithCategory> attendances) {
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.fragment_attendances_main_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new FlexibleAdapter<>(null, this);
        recyclerView.setAdapter(adapter);

        Map<LocalDate, AttendanceHeaderItem> headerItemMap = new HashMap<>();
        for (AttendanceWithCategory attendance : attendances) {
            AttendanceCategory category = attendance.category();
            LocalDate date = attendance.date();

            if (!category.presenceKind()) {
                if (headerItemMap.get(date) == null) {
                    headerItemMap.put(date, new AttendanceHeaderItem(date));
                }
                AttendanceItem subItem = new AttendanceItem(
                        headerItemMap.get(date),
                        attendance);
                headerItemMap.get(date)
                        .addSubItem(subItem);
            }
        }
        List<AttendanceHeaderItem> headers = new ArrayList<>(headerItemMap.values());
        Collections.sort(headers);
        for (AttendanceHeaderItem headerItem : headers) {
            adapter.addSection(headerItem);
        }
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
        AttendanceWithCategory attendance = attendanceItem.getAttendance();
        LibrusData.findFullAttendance(attendance)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(displayPopup(position));

        return true;
    }

    private Consumer<FullAttendance> displayPopup(int position) {
        return fullAttendance -> {
        View root = LayoutInflater.from(getContext()).inflate(R.layout.attendance_details, null);

        View subjectContainer = root.findViewById(R.id.attendance_details_subject_container);
        View addedByContainer = root.findViewById(R.id.attendance_details_added_by_container);

        TextView subjectValue = (TextView) root.findViewById(R.id.attendance_details_subject_value);
        TextView dateValue = (TextView) root.findViewById(R.id.attendance_details_date_value);
        TextView addedByValue = (TextView) root.findViewById(R.id.attendance_details_added_by_value);

            if (fullAttendance.subject() != null) {
                addedByContainer.setVisibility(View.VISIBLE);
                subjectValue.setText(fullAttendance.subject().name());
        } else {
            subjectContainer.setVisibility(View.GONE);
        }

        dateValue.setText(new StringBuilder()
                .append(fullAttendance.date().toString(getContext().getString(R.string.date_format_no_year), new Locale("pl")))
                .append(", ")
                .append(String.valueOf(fullAttendance.lessonNumber()))
                .append(". lekcja"));

            if (fullAttendance.addedBy() != null) {
            addedByContainer.setVisibility(View.VISIBLE);
                addedByValue.setText(fullAttendance.addedBy().name());
        } else {
            addedByContainer.setVisibility(View.GONE);
        }

        new MaterialDialog.Builder(getActivity())
                .title(fullAttendance.category().name())
                .customView(root, true)
                .positiveText(R.string.close)
                .dismissListener(dialog -> adapter.notifyItemChanged(position))
                .show();
        };
    }
}
