package pl.librus.client.grades;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import io.requery.Persistable;
import io.requery.sql.EntityDataStore;
import pl.librus.client.R;
import pl.librus.client.api.Reader;
import pl.librus.client.datamodel.Average;
import pl.librus.client.datamodel.AverageType;
import pl.librus.client.datamodel.Grade;
import pl.librus.client.datamodel.GradeCategory;
import pl.librus.client.datamodel.GradeComment;
import pl.librus.client.datamodel.GradeCommentType;
import pl.librus.client.datamodel.GradeType;
import pl.librus.client.datamodel.LibrusColor;
import pl.librus.client.datamodel.Subject;
import pl.librus.client.datamodel.Teacher;
import pl.librus.client.ui.MainApplication;
import pl.librus.client.ui.MainFragment;
import pl.librus.client.ui.MenuAction;
import pl.librus.client.ui.ReadAllMenuAction;

/**
 * A simple {@link Fragment} subclass.
 */
public class GradesFragment extends MainFragment implements FlexibleAdapter.OnItemClickListener {

    private final Comparator<GradeHeaderItem> headerComparator = new Comparator<GradeHeaderItem>() {
        @Override
        public int compare(GradeHeaderItem o1, GradeHeaderItem o2) {
            return o1.compareTo(o2);
        }
    };

    List<? extends MenuAction> actions = new ArrayList<>();
    private FlexibleAdapter<AbstractFlexibleItem> adapter;
    private OnSetupCompleteListener listener;
    private Reader reader;
    private EntityDataStore<Persistable> data;

    public GradesFragment() {
        // Required empty public constructor
    }

    public static GradesFragment newInstance() {
        return new GradesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        reader = new Reader(getContext());
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_grades, container, false);

        //Setup RecyclerView
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.fragment_grades_main_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new FlexibleAdapter<>(null, this);
        adapter.setAutoCollapseOnExpand(true)
                .setAutoScrollOnExpand(true);

        recyclerView.setAdapter(adapter);

        //Load subjects and make a header for each subject

        final Map<String, GradeHeaderItem> headers = new HashMap<>();
        data = MainApplication.getData();

        List<Subject> subjects = data.select(Subject.class).get().toList();
        for (Subject s : subjects) {
            Average average = MainApplication.getData()
                    .select(Average.class)
                    .where(AverageType.SUBJECT_ID.eq(s.id()))
                    .get()
                    .firstOrNull();
            headers.put(s.id(), new GradeHeaderItem(s, average));
        }

        //Load grades and sort them by their date
        List<Grade> grades = data.select(Grade.class)
                .orderBy(GradeType.DATE.desc())
                .get()
                .toList();
        actions = Lists.newArrayList(new ReadAllMenuAction(grades, getContext()));


        for (Grade grade : grades) {
            GradeItem item = getGradeItem(headers.get(grade.subject().id()), grade);
            headers.get(grade.subject().id()).addSubItem(item);
        }

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

        if (listener != null) listener.run();

        return root;
    }

    @Override
    public boolean onItemClick(final int position) {
        AbstractFlexibleItem item = adapter.getItem(position);
        if (item instanceof GradeItem) {
            GradeItem gradeItem = (GradeItem) item;
            Grade grade = gradeItem.getGrade();
            GradeCategory gc = gradeItem.getGradeCategory();
            GradeHeaderItem header = gradeItem.getHeader();

            @SuppressLint("InflateParams") View dialogLayout = LayoutInflater.from(getContext()).inflate(R.layout.grade_details, null, false);
            TextView gradeTextView = (TextView) dialogLayout.findViewById(R.id.grade_details_grade);
            TextView categoryTextView = (TextView) dialogLayout.findViewById(R.id.grade_details_category);
            TextView subjectTextView = (TextView) dialogLayout.findViewById(R.id.grade_details_subject);
            TextView dateTextView = (TextView) dialogLayout.findViewById(R.id.grade_details_date);
            TextView addedByTextView = (TextView) dialogLayout.findViewById(R.id.grade_details_addedBy);
            TextView weightTextView = (TextView) dialogLayout.findViewById(R.id.grade_details_weight);

            View commentContainer = dialogLayout.findViewById(R.id.grade_details_comment_container);
            View weightContainer = dialogLayout.findViewById(R.id.grade_details_weight_container);
            View addDateContainer = dialogLayout.findViewById(R.id.grade_details_add_date_container);

            gradeTextView.setText(grade.grade());
            categoryTextView.setText(gc.name());
            subjectTextView.setText(header.getSubject().name());
            weightTextView.setText(String.valueOf(gc.weight()));
            dateTextView.setText(grade.date().toString(getString(R.string.date_format_no_year), new Locale("pl")));
            if (grade.addDate().toLocalDate().isEqual(grade.date())) {
                addDateContainer.setVisibility(View.GONE);
            } else {
                addDateContainer.setVisibility(View.VISIBLE);
                TextView addDateTextView = (TextView) dialogLayout.findViewById(R.id.grade_details_add_date);
                addDateTextView.setText(grade.addDate().toString(getString(R.string.date_format_no_year), new Locale("pl")));
            }

            Grade.GradeType type = grade.type();
            weightContainer.setVisibility(type == Grade.GradeType.NORMAL ? View.VISIBLE : View.GONE);
            EntityDataStore<Persistable> data = MainApplication.getData();


            Teacher addedBy = data.findByKey(Teacher.class, grade.addedBy().id());
            addedByTextView.setText(addedBy.name());

            List<GradeComment> comments = data.select(GradeComment.class)
                    .where(GradeCommentType.GRADE_ID.eq(grade.id()))
                    .get()
                    .toList();
            if (comments != null && !comments.isEmpty()) {
                commentContainer.setVisibility(View.VISIBLE);
                TextView commentTextView = (TextView) dialogLayout.findViewById(R.id.grade_details_comment);
                commentTextView.setText(comments.get(0).text());
            } else {
                commentContainer.setVisibility(View.GONE);
            }
            new MaterialDialog.Builder(getContext())
                    .title(header.getSubject().name())
                    .customView(dialogLayout, true)
                    .positiveText(R.string.close)
                    .dismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            adapter.notifyItemChanged(position);
                        }
                    })
                    .show();

            reader.read(grade);

        } else //noinspection StatementWithEmptyBody
            if (item instanceof AverageItem) {
                //TODO
            }

        return false;
    }

    private GradeItem getGradeItem(GradeHeaderItem headerItem, Grade grade) {
        GradeCategory category = data.findByKey(GradeCategory.class, grade.category().id());
        LibrusColor color = category.color() != null ?
                data.findByKey(LibrusColor.class, category.color().id()) :
                new LibrusColor.Builder()
                        .rawColor("00000000")
                        .id("")
                        .name("")
                        .build();
        return new GradeItem(
                headerItem,
                grade,
                category,
                color
        );
    }

    @Override
    public void setOnSetupCompleteListener(OnSetupCompleteListener listener) {
        this.listener = listener;
    }

    @Override
    public void removeListener() {
        this.listener = null;
    }

    @Override
    public List<? extends MenuAction> getMenuItems() {
        return actions;
    }
}
