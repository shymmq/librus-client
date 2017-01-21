package pl.librus.client.grades;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jdeferred.DoneCallback;
import org.jdeferred.impl.DefaultDeferredManager;
import org.jdeferred.multiple.MultipleResults;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;
import pl.librus.client.R;
import pl.librus.client.api.GradeCategory;
import pl.librus.client.api.LibrusData;
import pl.librus.client.cache.GradeCache;
import pl.librus.client.cache.GradeCacheLoader;
import pl.librus.client.cache.GradeCategoriesLoader;
import pl.librus.client.ui.MainFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class GradesFragment extends Fragment implements MainFragment {


    private static final String ARG_DATA = "GradesFragment:data";
    private OnSetupCompleteListener listener;
    private RecyclerView recyclerView;
    private FlexibleAdapter<IFlexible> adapter;
    private GradeCache gradeCache;
    private List<GradeCategory> categories;

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


        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_grades, container, false);
        //Setup RecyclerView
        recyclerView = (RecyclerView) root.findViewById(R.id.fragment_grades_main_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new FlexibleAdapter<>(null);

        //Load all necessary data from cache
        new DefaultDeferredManager().when(
                new GradeCacheLoader(getContext())
                        .loadFromCache(null)
                        .done(new DoneCallback<GradeCache>() {
                            @Override
                            public void onDone(GradeCache result) {
                                gradeCache = result;
                            }
                        }),
                new GradeCategoriesLoader(getContext())
                        .loadFromCache(null)
                        .done(new DoneCallback<ArrayList<GradeCategory>>() {
                            @Override
                            public void onDone(ArrayList<GradeCategory> result) {
                                categories = result;
                            }
                        }))
                .done(new DoneCallback<MultipleResults>() {
                    @Override
                    public void onDone(MultipleResults result) {

                    }
                });
        Map<String, GradeSubjectItem> gradeSubjectItemMap = new HashMap<>();

//        for (Grade g : data.getGrades()) {
//            if (gradeSubjectItemMap.get(g.getSubjectId()) == null) {
//                //first appearance: add new subjectitem to map
//                gradeSubjectItemMap.put(
//                        g.getSubjectId(),
//                        new GradeSubjectItem(data.getSubjectMap().get(g.getSubjectId())));
//            }
//            GradeSubjectItem gradeSubjectItem = gradeSubjectItemMap.get(g.getSubjectId());
//            gradeSubjectItem.addSubItem(new GradeItem(gradeSubjectItem, g, data));
//        }
//        for (Average a : data.getAverages()) {
//            if (gradeSubjectItemMap.get(a.getSubjectId()) == null) {
//                //first appearance: add new subjectitem to map
//                gradeSubjectItemMap.put(
//                        a.getSubjectId(),
//                        new GradeSubjectItem(data.getSubjectMap().get(a.getSubjectId())));
//            }
//            GradeSubjectItem gradeSubjectItem = gradeSubjectItemMap.get(a.getSubjectId());
//            gradeSubjectItem.addSubItem(new AverageItem(gradeSubjectItem, a));
//        }
//        Map<String, List<TextGrade>> textGradeSubjectMap = new HashMap<>();
//        for (TextGrade t : data.getTextGrades()) {
//            String subjectId = t.getSubjectId();
//            if (!textGradeSubjectMap.containsKey(subjectId))
//                textGradeSubjectMap.put(subjectId, new ArrayList<TextGrade>());
//            textGradeSubjectMap.get(subjectId).add(t);
//        }
//        for (Map.Entry<String, List<TextGrade>> entry : textGradeSubjectMap.entrySet()) {
//            String subjectId = entry.getKey();
//            List<TextGrade> grades = entry.getValue();
//            GradeSubjectItem gradeSubjectItem = gradeSubjectItemMap.get(subjectId);
//            TextGradeSummaryItem summaryItem = new TextGradeSummaryItem(gradeSubjectItem);
//            for (TextGrade grade : grades) {
//                summaryItem.addSubItem(new TextGradeItem(grade));
//            }
//            gradeSubjectItem.addSubItem(summaryItem);
//        }
//
//        ArrayList<GradeSubjectItem> listItems = new ArrayList<>(gradeSubjectItemMap.values());
//        FlexibleAdapter<GradeSubjectItem> adapter = new FlexibleAdapter<>(listItems);
//        adapter.setAutoScrollOnExpand(true);
//        recyclerView.setAdapter(adapter);
//        if (listener != null) listener.onSetupComplete();
        return root;
    }

    @Override
    public void refresh(LibrusData cache) {

    }

    @Override
    public void setOnSetupCompleteListener(OnSetupCompleteListener listener) {
        this.listener = listener;
    }

}
