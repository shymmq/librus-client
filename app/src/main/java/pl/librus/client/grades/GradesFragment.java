package pl.librus.client.grades;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.librus.client.R;
import pl.librus.client.api.Grade;
import pl.librus.client.api.LibrusData;
import pl.librus.client.api.Subject;
import pl.librus.client.ui.MainFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class GradesFragment extends MainFragment {


    private static final String ARG_DATA = "GradesFragment:data";

    public GradesFragment() {
        // Required empty public constructor
    }

    public static GradesFragment newInstance(LibrusData data) {
        GradesFragment fragment = new GradesFragment();
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
        List<Grade> grades = data.getGrades();
        Map<String, Subject> subjectMap = data.getSubjectMap();
        Map<String, List<Grade>> subjects = new HashMap<>();
        List<GradeCategory> categories = new ArrayList<>();
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_grades, container, false);
        //Categorize grades based on subject
        for (Grade g : grades) {
            if (!subjects.containsKey(g.getSubjectId()))
                subjects.put(g.getSubjectId(), new ArrayList<Grade>());
            subjects.get(g.getSubjectId()).add(g);
        }
        for (Map.Entry<String, List<Grade>> entry : subjects.entrySet()) {
            categories.add(new GradeCategory(entry.getValue(), subjectMap.get(entry.getKey()).getName()));
        }
        Collections.sort(categories);
        //Setup RecyclerView
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.fragment_grades_main_list);
        GradeAdapter adapter = new GradeAdapter(categories, getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return root;
    }

    @Override
    public void refresh(LibrusData cache) {

    }
}
