package pl.librus.client.grades;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pl.librus.client.R;
import pl.librus.client.api.Average;
import pl.librus.client.api.Grade;
import pl.librus.client.api.GradeCategory;
import pl.librus.client.api.LibrusData;
import pl.librus.client.api.Subject;
import pl.librus.client.api.Teacher;

/**
 * Created by szyme on 09.12.2016. librus-client
 */

class GradeAdapter extends ExpandableRecyclerAdapter<GradeAdapter.GradeListCategory, GradeEntry, GradeAdapter.GradeCategoryViewHolder, ChildViewHolder> {

    @NonNull
    private final List<GradeListCategory> categories;
    private final int TYPE_GRADE = 11;
    private final int TYPE_AVERAGE = 12;
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
     * @param context    Context
     */
    private GradeAdapter(@NonNull List<GradeListCategory> parentList, Map<String, GradeCategory> gradeMap, Map<String, Subject> subjectMap, Map<String, Teacher> teacherMap, Context context) {
        super(parentList);
        this.categories = parentList;
        this.gradeMap = gradeMap;
        this.subjectMap = subjectMap;
        this.teacherMap = teacherMap;
        inflater = LayoutInflater.from(context);
    }


    static GradeAdapter fromLibrusData(LibrusData data) {
        List<Grade> grades = data.getGrades();
        List<Average> averages = data.getAverages();
        Map<String, Subject> subjectMap = data.getSubjectMap();
        Map<String, List<GradeEntry>> subjects = new HashMap<>();
        List<GradeListCategory> categories = new ArrayList<>();

        //Categorize grades by subject
        for (Grade g : grades) {
            if (!subjects.containsKey(g.getSubjectId()))
                subjects.put(g.getSubjectId(), new ArrayList<GradeEntry>());
            subjects.get(g.getSubjectId()).add(g);
        }
        for (Average a : averages) {
            if (!subjects.containsKey(a.getSubjectId()))
                subjects.put(a.getSubjectId(), new ArrayList<GradeEntry>());
            subjects.get(a.getSubjectId()).add(a);
        }
        for (Map.Entry<String, List<GradeEntry>> entry : subjects.entrySet()) {
            Collections.sort(entry.getValue(), Collections.<GradeEntry>reverseOrder());
            categories.add(new GradeListCategory(entry.getValue(), subjectMap.get(entry.getKey()).getName()));
        }
        Collections.sort(categories, Collections.<GradeListCategory>reverseOrder());
        return new GradeAdapter(categories, data.getGradeCategoriesMap(), data.getSubjectMap(), data.getTeacherMap(), data.getContext());
    }

    @NonNull
    @Override
    public GradeCategoryViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View root = inflater.inflate(R.layout.grade_category_item, parentViewGroup, false);
        return new GradeCategoryViewHolder(root);
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        switch (viewType) {
            case TYPE_GRADE:
                return new GradeViewHolder(inflater.inflate(R.layout.grade_item, childViewGroup, false));
            case TYPE_AVERAGE:
                return new AverageViewHolder(inflater.inflate(R.layout.average_item, childViewGroup, false));
            default:
                return new GradeViewHolder(inflater.inflate(R.layout.grade_item, childViewGroup, false));
        }
    }


    @Override
    public void onBindParentViewHolder(@NonNull GradeCategoryViewHolder parentViewHolder, int parentPosition, @NonNull GradeListCategory parent) {
        parentViewHolder.bind(parent);
    }

    @Override
    public void onBindChildViewHolder(@NonNull final ChildViewHolder childViewHolder, final int parentPosition, final int childPosition, @NonNull final GradeEntry child) {
        if (child instanceof Grade) {
//                viewType = TYPE_GRADE
            final Grade grade = (Grade) child;
            final GradeViewHolder gradeViewHolder = (GradeViewHolder) childViewHolder;
            gradeViewHolder.bind(grade, gradeMap.get(grade.getCategoryId()));
            gradeViewHolder.itemView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //TODO: handle retakes, show semester number
                            Context context = gradeViewHolder.itemView.getContext();
                            MaterialDialog.Builder builder = new MaterialDialog.Builder(context).title("Szczegóły oceny").positiveText("Zamknij");
                            LayoutInflater inflater = LayoutInflater.from(context);
                            View details = inflater.inflate(R.layout.grade_details, null);

                            TextView gradeView = (TextView) details.findViewById(R.id.grade_details_grade);
                            TextView weightView = (TextView) details.findViewById(R.id.grade_details_weight);
                            TextView categoryView = (TextView) details.findViewById(R.id.grade_details_category);
                            TextView subjectView = (TextView) details.findViewById(R.id.grade_details_subject);
                            TextView dateView = (TextView) details.findViewById(R.id.grade_details_date);
                            TextView addDateView = (TextView) details.findViewById(R.id.grade_details_addDate);
                            TextView addedByView = (TextView) details.findViewById(R.id.grade_details_addedBy);

                            gradeView.setText(grade.getGrade());
                            weightView.setText(String.valueOf(gradeMap.get(grade.getCategoryId()).getWeight()));
                            categoryView.setText(gradeMap.get(grade.getCategoryId()).getName());
                            subjectView.setText(subjectMap.get(grade.getSubjectId()).getName());
                            dateView.setText(grade.getDate().toString("EEEE, d MMMM yyyy", new Locale("pl")));
                            addDateView.setText(grade.getAddDate().toString("HH:mm, EEEE, d MMMM yyyy", new Locale("pl")));
                            addedByView.setText(teacherMap.get(grade.getAddedById()).getName());

                            builder.customView(details, true).show();
                        }

                    }
            );
        } else if (child instanceof Average) {
            AverageViewHolder averageViewHolder = (AverageViewHolder) childViewHolder;
            averageViewHolder.bind((Average) child);
        }

    }

    @Override
    public int getChildViewType(int parentPosition, int childPosition) {
        return categories.get(parentPosition).getChildList().get(childPosition) instanceof Grade ? TYPE_GRADE : TYPE_AVERAGE;
    }


    /**
     * Created by szyme on 09.12.2016. librus-client
     */

    private static class GradeViewHolder extends ChildViewHolder {

        private TextView grade, title, subtitle;

        GradeViewHolder(@NonNull View itemView) {
            super(itemView);
            grade = (TextView) itemView.findViewById(R.id.grade_item_grade);
            title = (TextView) itemView.findViewById(R.id.grade_item_title);
            subtitle = (TextView) itemView.findViewById(R.id.grade_item_subtitle);
        }

        void bind(Grade g, GradeCategory c) {
            grade.setText(g.getGrade());
            title.setText(c.getName());
            subtitle.setText(g.getDate().toString("d MMM.", new Locale("pl")));
        }
    }

    private static class AverageViewHolder extends ChildViewHolder {
        TextView average;

        /**
         * Default constructor.
         *
         * @param itemView The {@link View} being hosted in this ViewHolder
         */
        AverageViewHolder(@NonNull View itemView) {
            super(itemView);
            average = (TextView) itemView.findViewById(R.id.average_item_average);
        }

        void bind(Average a) {
            average.setText(String.valueOf(a.getFullYear()));
        }
    }

    /**
     * Created by szyme on 09.12.2016. librus-client
     */

    static class GradeCategoryViewHolder extends ParentViewHolder {

        TextView title;
        TextView content;
        View root, arrow;

        GradeCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.grade_category_item_root);
            //        divider = itemView.findViewById(R.id.grade_category_item_divider);
            title = (TextView) itemView.findViewById(R.id.grade_category_item_title);
            content = (TextView) itemView.findViewById(R.id.grade_category_item_content);
            arrow = itemView.findViewById(R.id.grade_category_item_arrow);
        }

        void bind(GradeListCategory category) {
            title.setText(category.getTitle());
            int size = category.getChildList().size();
            String gradeCount;
            if (size == 0) gradeCount = "Brak ocen";
            else if (size == 1) gradeCount = "1 ocena";
            else if (2 <= size && size <= 4) gradeCount = size + " oceny";
            else if (5 <= size) gradeCount = size + " ocen";
            else gradeCount = "Oceny: " + size;
            content.setText(gradeCount);

            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isExpanded()) {
                        ObjectAnimator.ofFloat(arrow, "rotation", 180f, 0f).start();
                        collapseView();
                    } else {
                        ObjectAnimator.ofFloat(arrow, "rotation", 0f, 180f).start();
                        expandView();
                    }
                }
            });
        }

        @Override
        public boolean shouldItemViewClickToggleExpansion() {
            return false;
        }
    }

    /**
     * Created by szyme on 11.12.2016. librus-client
     */
    static class GradeListCategory implements Parent<GradeEntry>, Comparable {

        private List<GradeEntry> gradeEntries;
        private String title;

        GradeListCategory(List<GradeEntry> grades, String title) {
            this.gradeEntries = grades;
            this.title = title;
        }

        @Override
        public List<GradeEntry> getChildList() {
            return gradeEntries;
        }

        @Override
        public boolean isInitiallyExpanded() {
            return false;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public int compareTo(@NonNull Object o) {
            return title.compareTo(((GradeListCategory) o).getTitle());
        }
    }
}
