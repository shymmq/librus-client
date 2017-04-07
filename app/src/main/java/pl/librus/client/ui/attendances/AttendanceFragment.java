package pl.librus.client.ui.attendances;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

import javax.inject.Inject;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;
import pl.librus.client.MainApplication;
import pl.librus.client.R;
import pl.librus.client.domain.attendance.AttendanceCategory;
import pl.librus.client.domain.attendance.FullAttendance;
import pl.librus.client.domain.subject.Subject;
import pl.librus.client.presentation.AttendancesPresenter;
import pl.librus.client.util.LibrusUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttendanceFragment extends Fragment implements FlexibleAdapter.OnItemClickListener, AttendancesView {

    private FlexibleAdapter<IFlexible> adapter;
    private View root;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    @Inject
    AttendancesPresenter presenter;

    public AttendanceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainApplication.getMainActivityComponent()
                .inject(this);

        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_attendances, container, false);

        recyclerView = (RecyclerView) root.findViewById(R.id.fragment_attendances_main_list);
        refreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.fragment_attendances_refresh_layout);
        adapter = new FlexibleAdapter<>(null, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        refreshLayout.setColorSchemeResources(R.color.md_blue_grey_400, R.color.md_blue_grey_500, R.color.md_blue_grey_600);
        refreshLayout.setOnRefreshListener(presenter::reload);

        presenter.attachView(this);

        return root;
    }

    @Override
    public boolean onItemClick(int position) {
        IFlexible item = adapter.getItem(position);
        if (!(item instanceof AttendanceItem)) return true;
        presenter.attendanceClicked(((AttendanceItem) item).getAttendance());
        return true;
    }

    @Override
    public void displayPopup(FullAttendance fullAttendance) {
        View root = LayoutInflater.from(getContext()).inflate(R.layout.attendance_details, null);

        View subjectContainer = root.findViewById(R.id.attendance_details_subject_container);
        View addedByContainer = root.findViewById(R.id.attendance_details_added_by_container);

        TextView subjectValue = (TextView) root.findViewById(R.id.attendance_details_subject_value);
        TextView dateValue = (TextView) root.findViewById(R.id.attendance_details_date_value);
        TextView addedByValue = (TextView) root.findViewById(R.id.attendance_details_added_by_value);

        LibrusUtils.setTextViewValue(subjectContainer, subjectValue,
                fullAttendance.subject().transform(Subject::name));

        dateValue.setText(new StringBuilder()
                .append(fullAttendance.date().toString(getContext().getString(R.string.date_format_no_year), new Locale("pl")))
                .append(", ")
                .append(String.valueOf(fullAttendance.lessonNumber()))
                .append(". lekcja"));

        LibrusUtils.setTextViewValue(addedByContainer, addedByValue, fullAttendance.addedByName());

        new MaterialDialog.Builder(getActivity())
                .title(fullAttendance.category().name())
                .customView(root, true)
                .positiveText(R.string.close)
                .show();
    }

    @Override
    public void display(List<FullAttendance> attendances) {
        adapter.clear();

        Map<LocalDate, AttendanceHeaderItem> headerItemMap = new HashMap<>();
        for (FullAttendance attendance : attendances) {
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
    public void setRefreshing(boolean b) {
        refreshLayout.setRefreshing(b);
    }
}
