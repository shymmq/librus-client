package pl.librus.client.grades;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.librus.client.R;
import pl.librus.client.api.Grade;
import pl.librus.client.api.LibrusData;
import pl.librus.client.api.Subject;
import pl.librus.client.sql.LibrusDbContract;
import pl.librus.client.sql.LibrusDbHelper;
import pl.librus.client.ui.MainFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class GradesFragment extends Fragment implements MainFragment {


    private static final String ARG_DATA = "GradesFragment:data";

    private RecyclerView recyclerView;
    private GradeAdapter adapter;

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
        adapter.setAutoCollapseOnExpand(true)
                .setAutoScrollOnExpand(true);
        recyclerView.setAdapter(adapter);
        //Load all necessary data from cache
        LibrusDbHelper dbHelper = new LibrusDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        final Map<String, GradeHeaderItem> headers = new HashMap<>();


        //Load subjects and make a header for each subject
        Cursor subjectCursor = db.query(LibrusDbContract.Subjects.TABLE_NAME, null, null, null, null, null, null);
        while (subjectCursor.moveToNext()) {
            Subject s = new Subject(
                    subjectCursor.getString(subjectCursor.getColumnIndexOrThrow(LibrusDbContract.Subjects.COLUMN_NAME_ID)),
                    subjectCursor.getString(subjectCursor.getColumnIndexOrThrow(LibrusDbContract.Subjects.COLUMN_NAME_NAME))
            );
            headers.put(s.getId(), new GradeHeaderItem(s));
        }
        subjectCursor.close();

        List<Grade> grades = dbHelper.getGrades();

        for (Grade grade : grades) {
            GradeItem item = new GradeItem(
                    headers.get(grade.getSubjectId()),
                    grade,
                    dbHelper.getGradeCategory(grade.getCategoryId()));
            headers.get(grade.getSubjectId()).addSubItem(item);
        }
//        for (Average average : gradeCache.getAverages()) {
//            AverageItem item = new AverageItem(
//                    headers.get(average.getSubjectId()),
//                    average);
//            headers.get(average.getSubjectId()).addSubItem(item);
//        }
        final Comparator<GradeHeaderItem> headerComparator = new Comparator<GradeHeaderItem>() {
            @Override
            public int compare(GradeHeaderItem o1, GradeHeaderItem o2) {
                return o1.compareTo(o2);
            }
        };

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (GradeHeaderItem header : headers.values()) {
                    adapter.addSection(header.sort(), headerComparator);
                }
                adapter.showAllHeaders()
                        .collapseAll();
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
}
