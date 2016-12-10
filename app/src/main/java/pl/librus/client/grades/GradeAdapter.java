package pl.librus.client.grades;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.librus.client.R;
import pl.librus.client.api.Grade;
import pl.librus.client.api.GradeCategory;
import pl.librus.client.api.LibrusData;
import pl.librus.client.api.Subject;

/**
 * Created by szyme on 09.12.2016. librus-client
 */

class GradeAdapter extends ExpandableRecyclerAdapter<GradesFragment.GradeListCategory, Grade, GradeCategoryViewHolder, GradeViewHolder> {

    @NonNull
    private final List<GradesFragment.GradeListCategory> categories;
    private LayoutInflater inflater;
    private Map<String, GradeCategory> gradeMap;

    /**
     * Primary constructor. Sets up {@link #mParentList} and {@link #mFlatItemList}.
     * <p>
     * Any changes to {@link #mParentList} should be made on the original instance, and notified via
     * {@link #notifyParentInserted(int)}
     * {@link #notifyParentRemoved(int)}
     * {@link #notifyParentChanged(int)}
     * {@link #notifyParentRangeInserted(int, int)}
     * {@link #notifyChildInserted(int, int)}
     * {@link #notifyChildRemoved(int, int)}
     * {@link #notifyChildChanged(int, int)}
     * methods and not the notify methods of RecyclerView.Adapter.
     *
     * @param parentList List of all parents to be displayed in the RecyclerView that this
     *                   adapter is linked to
     * @param context
     */
    GradeAdapter(@NonNull List<GradesFragment.GradeListCategory> parentList, Map<String, GradeCategory> gradeMap, Context context) {
        super(parentList);
        this.categories = parentList;
        this.gradeMap = gradeMap;
        inflater = LayoutInflater.from(context);
    }


    static GradeAdapter fromLibrusData(LibrusData data) {
        List<Grade> grades = data.getGrades();
        Map<String, Subject> subjectMap = data.getSubjectMap();
        Map<String, List<Grade>> subjects = new HashMap<>();
        List<GradesFragment.GradeListCategory> categories = new ArrayList<>();

        //Categorize grades by subject
        for (Grade g : grades) {
            if (!subjects.containsKey(g.getSubjectId()))
                subjects.put(g.getSubjectId(), new ArrayList<Grade>());
            subjects.get(g.getSubjectId()).add(g);
        }
        for (Map.Entry<String, List<Grade>> entry : subjects.entrySet()) {
            categories.add(new GradesFragment.GradeListCategory(entry.getValue(), subjectMap.get(entry.getKey()).getName()));
        }
        Collections.sort(categories);
        return new GradeAdapter(categories, data.getGradeCategoriesMap(), data.getContext());
    }

    @NonNull
    @Override
    public GradeCategoryViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View root = inflater.inflate(R.layout.two_line_list_item, parentViewGroup, false);
        return new GradeCategoryViewHolder(root);
    }

    @NonNull
    @Override
    public GradeViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View root = inflater.inflate(R.layout.grade_item, childViewGroup, false);
        return new GradeViewHolder(root);
    }


    @Override
    public void onBindParentViewHolder(@NonNull GradeCategoryViewHolder parentViewHolder, int parentPosition, @NonNull GradesFragment.GradeListCategory parent) {
        parentViewHolder.bind(parent);
    }

    @Override
    public void onBindChildViewHolder(@NonNull GradeViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull Grade child) {
        childViewHolder.bind(child, gradeMap.get(child.getCategoryId()));
    }
}
