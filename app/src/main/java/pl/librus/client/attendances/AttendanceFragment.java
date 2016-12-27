package pl.librus.client.attendances;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.librus.client.R;
import pl.librus.client.api.LibrusData;
import pl.librus.client.ui.MainFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttendanceFragment extends Fragment implements MainFragment {


    private static final String ARG_DATA = "AttendanceFragment:data";

    public AttendanceFragment() {
        // Required empty public constructor
    }

    public static AttendanceFragment newInstance(LibrusData data) {
        AttendanceFragment fragment = new AttendanceFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get data from arguments
        LibrusData data = (LibrusData) getArguments().getSerializable(ARG_DATA);
        assert data != null;
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_attendance, container, false);
        //Setup RecyclerView
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.fragment_attendances_main_list);
        AttendanceAdapter adapter = AttendanceAdapter.fromLibrusData(data);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.getItemAnimator().setAddDuration(150);
        recyclerView.getItemAnimator().setRemoveDuration(150);
        recyclerView.getItemAnimator().setMoveDuration(150);
        recyclerView.getItemAnimator().setChangeDuration(150);

        return root;
    }

    @Override
    public void refresh(LibrusData cache) {

    }

}
