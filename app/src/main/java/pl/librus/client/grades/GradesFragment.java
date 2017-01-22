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

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import pl.librus.client.R;
import pl.librus.client.api.Grade;
import pl.librus.client.api.GradeCategory;
import pl.librus.client.api.LibrusData;
import pl.librus.client.api.Subject;
import pl.librus.client.api.Teacher;
import pl.librus.client.cache.GradeCache;
import pl.librus.client.cache.GradeCacheLoader;
import pl.librus.client.cache.GradeCategoriesLoader;
import pl.librus.client.cache.SubjectLoader;
import pl.librus.client.cache.TeacherLoader;
import pl.librus.client.ui.MainFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class GradesFragment extends Fragment implements MainFragment {


    private static final String ARG_DATA = "GradesFragment:data";
    private OnSetupCompleteListener listener;

    private RecyclerView recyclerView;
    private GradeAdapter adapter;

    private GradeCache gradeCache;
    private Map<String, GradeCategory> categoryMap = new HashMap<>();
    private Map<String, Subject> subjectMap = new HashMap<>();
    private Map<String, Teacher> teacherMap = new HashMap<>();
    private ArrayList<Subject> subjects;

    public GradesFragment() {
        // Required empty public constructor
    }

    public static GradesFragment newInstance() {
        return new GradesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_grades, container, false);
        //Setup RecyclerView
        recyclerView = (RecyclerView) root.findViewById(R.id.fragment_grades_main_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new GradeAdapter(null);
        adapter.setDisplayHeadersAtStartUp(true)
                .setAutoCollapseOnExpand(true);
        recyclerView.setAdapter(adapter);
        //Load all necessary data from cache
        new DefaultDeferredManager().when(
                new GradeCacheLoader(getContext())
                        .loadFromCache()
                        .done(new DoneCallback<GradeCache>() {
                            @Override
                            public void onDone(GradeCache result) {
                                gradeCache = result;
                            }
                        }),
                new GradeCategoriesLoader(getContext())
                        .loadFromCache()
                        .done(new DoneCallback<ArrayList<GradeCategory>>() {
                            @Override
                            public void onDone(ArrayList<GradeCategory> result) {
                                for (GradeCategory gc : result)
                                    categoryMap.put(gc.getId(), gc);
                            }
                        }),
                new SubjectLoader(getContext())
                        .loadFromCache()
                        .done(new DoneCallback<ArrayList<Subject>>() {
                            @Override
                            public void onDone(ArrayList<Subject> result) {
                                subjects = result;
                                for (Subject s : result)
                                    subjectMap.put(s.getId(), s);
                            }
                        }),
                new TeacherLoader(getContext())
                        .loadFromCache()
                        .done(new DoneCallback<ArrayList<Teacher>>() {
                            @Override
                            public void onDone(ArrayList<Teacher> result) {
                                for (Teacher t : result)
                                    teacherMap.put(t.getId(), t);
                            }
                        }))
                .done(new DoneCallback<MultipleResults>() {
                    @Override
                    public void onDone(MultipleResults result) {
                        //All necessary data was loaded from cache
                        final Map<String, GradeHeaderItem> headers = new HashMap<>();
                        for (Subject s : subjects) {
                            headers.put(s.getId(), new GradeHeaderItem(s));
                        }
                        final List<AbstractFlexibleItem> items = new ArrayList<>();
                        for (Grade grade : gradeCache.getGrades()) {
                            GradeItem item = new GradeItem(
                                    headers.get(grade.getSubjectId()),
                                    grade,
                                    categoryMap.get(grade.getCategoryId()));
                            headers.get(grade.getSubjectId()).addSubItem(item);
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (GradeHeaderItem header : headers.values()) {
                                    adapter.addSection(header);
                                }
                                adapter.showAllHeaders()
                                        .collapseAll();
                            }
                        });
//                        Map<String, GradeHeaderItem> listHeaders = new HashMap<>();
//                        for (Grade grade : gradeCache.getGrades()) {
//                            if (listHeaders.get(grade.getSubjectId()) == null) {
//                                //first appearance: add new subjectitem to map
//                                Subject subject = subjectMap.get(grade.getSubjectId());
//                                listHeaders.put(
//                                        grade.getSubjectId(),
//                                        new GradeHeaderItem(subject));
//                            }
//                            GradeHeaderItem gradeHeaderItem = listHeaders.get(grade.getSubjectId());
//                            GradeCategory category = categoryMap.get(grade.getCategoryId());
//                            gradeHeaderItem.addSubItem(new GradeItem(gradeHeaderItem, grade, category));
//                        }


                    }
                });

//        for (Average a : data.getAverages()) {
//            if (gradeSubjectItemMap.get(a.getSubjectId()) == null) {
//                //first appearance: add new subjectitem to map
//                gradeSubjectItemMap.put(
//                        a.getSubjectId(),
//                        new GradeHeaderItem(data.getSubjectMap().get(a.getSubjectId())));
//            }
//            GradeHeaderItem gradeSubjectItem = gradeSubjectItemMap.get(a.getSubjectId());
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
//            GradeHeaderItem gradeSubjectItem = gradeSubjectItemMap.get(subjectId);
//            TextGradeSummaryItem summaryItem = new TextGradeSummaryItem(gradeSubjectItem);
//            for (TextGrade grade : grades) {
//                summaryItem.addSubItem(new TextGradeItem(grade));
//            }
//            gradeSubjectItem.addSubItem(summaryItem);
//        }
//
//        ArrayList<GradeHeaderItem> listItems = new ArrayList<>(gradeSubjectItemMap.values());
//        FlexibleAdapter<GradeHeaderItem> adapter = new FlexibleAdapter<>(listItems);
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
