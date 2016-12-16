package pl.librus.client.attendances;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.bignerdranch.expandablerecyclerview.model.Parent;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pl.librus.client.R;
import pl.librus.client.api.Announcement;
import pl.librus.client.api.Attendance;
import pl.librus.client.api.AttendanceCategory;
import pl.librus.client.api.Lesson;
import pl.librus.client.api.LibrusData;
import pl.librus.client.api.Subject;
import pl.librus.client.api.Teacher;

/**
 * Created by Adam on 15.12.2016.
 */

class AttendanceAdapter extends ExpandableRecyclerAdapter<AttendanceAdapter.Category, Attendance, AttendanceAdapter.AttendanceCategoryViewHolder, ChildViewHolder>{

    @NonNull
    private final List<Category> categories;

    private List<Attendance> attendances;
    private Map<String, AttendanceCategory> attendanceCategoryMap = new HashMap<>();
    private LayoutInflater inflater;
    private LibrusData data;

    private AttendanceAdapter(@NonNull List<Category> parentList, LibrusData data, Context context) {
        super(parentList);
        this.categories = parentList;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    static AttendanceAdapter fromLibrusData(LibrusData data) {
        List<Attendance> attendances = new ArrayList<>();
        attendances.addAll(data.getAttendances());
        Map<String, AttendanceCategory> attendanceCategoryMap = data.getAttendanceCategoryMap();
        Map<String, Subject> subjectMap = data.getSubjectMap();
        List<Category> categories = new ArrayList<>();
        List<String> categoryTitles = new ArrayList<>();
        for(int i = 0; i < attendances.size(); i++) {
            String attendanceMonth = attendances.get(i).getDate().toString("MMMM", new Locale("pl"));
            for(int j = 0; j < categories.size(); j++) {
                categoryTitles.add(categories.get(i).getTitle());
            }
            if(!categoryTitles.contains(attendanceMonth)) {
                categories.add(new Category(attendances, attendanceMonth));
            }
        }

        return new AttendanceAdapter(categories, data, data.getContext());
    }

    @NonNull
    @Override
    public AttendanceCategoryViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View root = inflater.inflate(R.layout.attendance_category_item, parentViewGroup, false);
        return new AttendanceCategoryViewHolder(root);
    }

    @NonNull
    public ChildViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        return new AttendanceViewHolder(inflater.inflate(R.layout.grade_item, childViewGroup, false));
    }

    @Override
    public void onBindParentViewHolder(@NonNull AttendanceCategoryViewHolder parentViewHolder, int parentPosition, @NonNull Category parent) {
        parentViewHolder.bind(parent);
    }

    @Override
    public void onBindChildViewHolder(@NonNull final ChildViewHolder childViewHolder, final int parentPosition, final int childPosition, @NonNull final Attendance child) {
        Map<String, AttendanceCategory> attendanceCategoryMap = data.getAttendanceCategoryMap();
        final Attendance attendance = (Attendance) child;
        final AttendanceViewHolder gradeViewHolder = (AttendanceViewHolder) childViewHolder;
        gradeViewHolder.bind(attendance, attendanceCategoryMap.get(attendance.getTypeId()), data);
    }

    /*
    @Override
    public int getChildViewType(int parentPosition, int childPosition) {
        Attendance child = categories.get(parentPosition).getChildList().get(childPosition);
    }
    */

    private static class AttendanceViewHolder extends ChildViewHolder {

        private TextView lessonView, dateView, typeView, shortTypeView;

        AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            lessonView = (TextView) itemView.findViewById(R.id.attendance_item_lesson);
            dateView = (TextView) itemView.findViewById(R.id.attendance_item_date);
            typeView = (TextView) itemView.findViewById(R.id.attendance_item_type);
            shortTypeView = (TextView) itemView.findViewById(R.id.attendance_item_shortType);
        }

        void bind(final Attendance attendance, final AttendanceCategory attendanceCategory, final LibrusData data) {
            final Map<String, Subject> subjectMap = data.getSubjectMap();
            final Map<String, AttendanceCategory> attendanceCategoryMap = data.getAttendanceCategoryMap();
            final Map<String, String> lessonMap = data.getLessonMap();

            lessonView.setText(subjectMap.get(lessonMap.get(attendance.getLessonId())).getName());
            dateView.setText(attendance.getDate().toString("EEEE, d MMMM yyyy", new Locale("pl")));
            typeView.setText(attendanceCategoryMap.get(attendance.getTypeId()).getName());
            shortTypeView.setText(attendanceCategoryMap.get(attendance.getTypeId()).getShortName());
        }
    }

    static class AttendanceCategoryViewHolder extends ParentViewHolder {

        TextView title;
        TextView content;
        View root, arrow;

        AttendanceCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.attendance_category_item_root);
            //        divider = itemView.findViewById(R.id.grade_category_item_divider);
            title = (TextView) itemView.findViewById(R.id.attendance_category_item_title);
            content = (TextView) itemView.findViewById(R.id.attendance_category_item_content);
            arrow = itemView.findViewById(R.id.attendance_category_item_arrow);
        }

        void bind(Category category) {
            title.setText(category.getTitle());
            int size = category.getChildList().size();
            String attendancesCount;
            if (size == 0) attendancesCount = "Brak nieobecności";
            else if (size == 1) attendancesCount = "1 nieobecność";
            else if (2 <= size && size <= 4) attendancesCount = size + " nieobecności";
            else if (5 <= size) attendancesCount = size + " nieobecności";
            else attendancesCount = "Nieobecności: " + size;
            content.setText(attendancesCount);

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

    static class Category implements Parent<Attendance>, Comparable {

        private List<Attendance> attendances;
        private String title;

        Category(List<Attendance> attendances, String title) {
            this.attendances = attendances;
            this.title = title;
        }

        @Override
        public List<Attendance> getChildList() {
            return attendances;
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
            return -title.compareTo(((Category) o).getTitle());
        }
    }
}

