package pl.librus.client.ui.grades;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import java8.util.stream.StreamSupport;
import pl.librus.client.MainApplication;
import pl.librus.client.R;
import pl.librus.client.domain.grade.FullGrade;
import pl.librus.client.domain.grade.GradesForSubject;
import pl.librus.client.domain.subject.FullSubject;
import pl.librus.client.presentation.GradesPresenter;
import pl.librus.client.presentation.MainFragmentPresenter;
import pl.librus.client.ui.MainFragment;
import pl.librus.client.util.LibrusUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class GradesFragment extends MainFragment implements FlexibleAdapter.OnItemClickListener, GradesView {

    private final Comparator<GradeHeaderItem> headerComparator = GradeHeaderItem::compareTo;

    private FlexibleAdapter<AbstractFlexibleItem> adapter;
    private SwipeRefreshLayout refreshLayout;

    @Inject
    GradesPresenter presenter;

    public GradesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_grades, container, false);

        //Setup RecyclerView
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.fragment_grades_main_list);
        refreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.fragment_grades_refresh);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.getItemAnimator().setChangeDuration(0);
        refreshLayout.setColorSchemeResources(R.color.md_blue_grey_400, R.color.md_blue_grey_500, R.color.md_blue_grey_600);
        adapter = new FlexibleAdapter<>(null, this);

        //TODO fix auto collapse
        adapter.setAutoScrollOnExpand(true)
                .setAutoCollapseOnExpand(true)
                .setMinCollapsibleLevel(1);

        recyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    protected void injectPresenter() {
        MainApplication.getMainActivityComponent()
                .inject(this);
        refreshLayout.setOnRefreshListener(presenter::reload);
        presenter.attachView(this);
    }

    @Override
    protected MainFragmentPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void updateGrade(int position) {
        adapter.notifyItemChanged(position);
    }

    @Override
    public boolean onItemClick(final int position) {
        AbstractFlexibleItem item = adapter.getItem(position);
        if (item instanceof GradeItem) {
            GradeItem gradeItem = (GradeItem) item;
            presenter.gradeClicked(position, gradeItem.getGrade());

        } else //noinspection StatementWithEmptyBody
            if (item instanceof AverageItem) {
                //TODO
            }

        return false;
    }

    @Override
    public void displayGradeDetails(FullGrade grade) {
        @SuppressLint("InflateParams")
        View dialogLayout = LayoutInflater.from(getContext()).inflate(R.layout.grade_details, null, false);
        TextView gradeTextView = (TextView) dialogLayout.findViewById(R.id.grade_details_grade);
        TextView categoryTextView = (TextView) dialogLayout.findViewById(R.id.grade_details_category);
        TextView subjectTextView = (TextView) dialogLayout.findViewById(R.id.grade_details_subject);
        TextView dateTextView = (TextView) dialogLayout.findViewById(R.id.grade_details_date);
        TextView addedByTextView = (TextView) dialogLayout.findViewById(R.id.grade_details_addedBy);
        TextView weightTextView = (TextView) dialogLayout.findViewById(R.id.grade_details_weight);

        View commentContainer = dialogLayout.findViewById(R.id.grade_details_comment_container);
        View weightContainer = dialogLayout.findViewById(R.id.grade_details_weight_container);
        View addDateContainer = dialogLayout.findViewById(R.id.grade_details_add_date_container);
        View addedByContainer = dialogLayout.findViewById(R.id.grade_details_added_by_container);

        gradeTextView.setText(grade.grade());
        categoryTextView.setText(grade.category().name());
        subjectTextView.setText(grade.subject().name());
        dateTextView.setText(grade.date().toString(getString(R.string.date_format_no_year), new Locale("pl")));
        if (grade.addDate().toLocalDate().isEqual(grade.date())) {
            addDateContainer.setVisibility(View.GONE);
        } else {
            addDateContainer.setVisibility(View.VISIBLE);
            TextView addDateTextView = (TextView) dialogLayout.findViewById(R.id.grade_details_add_date);
            addDateTextView.setText(grade.addDate().toString(getString(R.string.date_format_no_year), new Locale("pl")));
        }

        LibrusUtils.setTextViewValue(
                weightContainer,
                weightTextView,
                grade.category()
                        .weight()
                        .transform(Object::toString));

        LibrusUtils.setTextViewValue(addedByContainer, addedByTextView, grade.addedByName());

        if (grade.comments() != null && !grade.comments().isEmpty()) {
            commentContainer.setVisibility(View.VISIBLE);
            TextView commentTextView = (TextView) dialogLayout.findViewById(R.id.grade_details_comment);
            commentTextView.setText(grade.comments().get(0).text());
        } else {
            commentContainer.setVisibility(View.GONE);
        }
        new MaterialDialog.Builder(getContext())
                .title(grade.subject().name())
                .customView(dialogLayout, true)
                .positiveText(R.string.close)
                .show();

    }

    @Override
    public void setRefreshing(boolean b) {
        refreshLayout.setRefreshing(b);
    }

    @Override
    public void display(List<GradesForSubject> content) {
        adapter.clear();

        for (GradesForSubject gfs : content) {
            FullSubject s = gfs.subject();

            final GradeHeaderItem headerItem = new GradeHeaderItem(s, getContext());

            StreamSupport.stream(gfs.grades())
                    .sorted((g1, g2) -> g2.date().compareTo(g1.date()))
                    .forEach(grade ->
                            headerItem.addSubItem(new GradeItem(headerItem, grade)));

            adapter.addSection(headerItem, headerComparator);
        }
    }
}
