package pl.librus.client.grades;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pl.librus.client.R;
import pl.librus.client.api.Grade;
import pl.librus.client.api.GradeCategory;
import pl.librus.client.api.Lesson;
import pl.librus.client.api.LibrusData;
import pl.librus.client.api.Subject;
import pl.librus.client.api.Teacher;

/**
 * Created by szyme on 09.12.2016. librus-client
 */

class GradeAdapter extends ExpandableRecyclerAdapter<GradesFragment.GradeListCategory, Grade, GradeCategoryViewHolder, GradeViewHolder> {

    @NonNull
    private final List<GradesFragment.GradeListCategory> categories;
    private LayoutInflater inflater;
    private Map<String, GradeCategory> gradeMap;
    private Map<String, Subject> subjectMap;
    private Map<String, Teacher> teacherMap;

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
    GradeAdapter(@NonNull List<GradesFragment.GradeListCategory> parentList, Map<String, GradeCategory> gradeMap, Map<String, Subject> subjectMap, Map<String, Teacher> teacherMap, Context context) {
        super(parentList);
        this.categories = parentList;
        this.gradeMap = gradeMap;
        this.subjectMap = subjectMap;
        this.teacherMap = teacherMap;
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
        return new GradeAdapter(categories, data.getGradeCategoriesMap(), data.getSubjectMap(), data.getTeacherMap(), data.getContext());
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
    public void onBindChildViewHolder(@NonNull GradeViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull final Grade child) {
        childViewHolder.bind(child, gradeMap.get(child.getCategoryId()));
        RelativeLayout background = (RelativeLayout) childViewHolder.itemView.getRootView();
        final Context context = background.getContext();
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: handle retakes, show semester number
                MaterialDialog.Builder builder = new MaterialDialog.Builder(context).title("Szczegóły oceny").positiveText("Zamknij");
                LayoutInflater inflater = LayoutInflater.from(context);
                View details = inflater.inflate(R.layout.grade_details, null);

                TextView gradeView = (TextView) details.findViewById(R.id.grade_detials_grade);
                TextView weightView = (TextView) details.findViewById(R.id.grade_details_weight);
                TextView categoryView = (TextView) details.findViewById(R.id.grade_details_category);
                TextView subjectView = (TextView) details.findViewById(R.id.grade_details_subject);
                TextView dateView = (TextView) details.findViewById(R.id.grade_details_date);
                TextView addDateView = (TextView) details.findViewById(R.id.grade_details_addDate);
                TextView addedByView = (TextView) details.findViewById(R.id.grade_details_addedBy);

                gradeView.setText(child.getGrade());
                weightView.setText(String.valueOf(gradeMap.get(child.getCategoryId()).getWeight()));
                categoryView.setText(gradeMap.get(child.getCategoryId()).getName());
                subjectView.setText(subjectMap.get(child.getSubjectId()).getName());
                dateView.setText(child.getDate().toString("EEEE, d MMMM yyyy", new Locale("pl")));
                addDateView.setText(child.getAddDate().toString("EEEE, HH:mm, d MMMM yyyy", new Locale("pl")));
                addedByView.setText(teacherMap.get(child.getAddedById()).getName());

                builder.customView(details, true).show();
            }
        });
    }
}
