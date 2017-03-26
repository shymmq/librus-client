package pl.librus.client.grades;


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
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java8.util.Optional;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import pl.librus.client.LibrusUtils;
import pl.librus.client.R;
import pl.librus.client.api.LibrusData;
import pl.librus.client.api.Reader;
import pl.librus.client.datamodel.Teacher;
import pl.librus.client.datamodel.grade.EnrichedGrade;
import pl.librus.client.datamodel.grade.FullGrade;
import pl.librus.client.datamodel.grade.Grade;
import pl.librus.client.datamodel.grade.GradeCategory;
import pl.librus.client.datamodel.grade.GradeComment;
import pl.librus.client.datamodel.subject.FullSubject;
import pl.librus.client.datamodel.subject.Subject;
import pl.librus.client.sql.UpdateHelper;
import pl.librus.client.ui.BaseFragment;
import pl.librus.client.ui.MenuAction;
import pl.librus.client.ui.ReadAllMenuAction;

/**
 * A simple {@link Fragment} subclass.
 */
public class GradesFragment extends BaseFragment implements FlexibleAdapter.OnItemClickListener {

    private final Comparator<GradeHeaderItem> headerComparator = GradeHeaderItem::compareTo;

    private FlexibleAdapter<AbstractFlexibleItem> adapter;
    private Reader reader;
    private java8.util.function.Consumer<List<? extends MenuAction>> actionsHandler;
    private SwipeRefreshLayout refreshLayout;

    public GradesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        reader = new Reader(getContext());
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_grades, container, false);

        //Setup RecyclerView
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.fragment_grades_main_list);
        refreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.fragment_grades_refresh);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.getItemAnimator().setChangeDuration(0);
        refreshLayout.setOnRefreshListener(this::refresh);
        refreshLayout.setColorSchemeResources(R.color.md_blue_grey_400, R.color.md_blue_grey_500, R.color.md_blue_grey_600);
        adapter = new FlexibleAdapter<>(null, this);

        //TODO fix auto collapse
        adapter.setAutoScrollOnExpand(true)
                .setAutoCollapseOnExpand(true)
                .setMinCollapsibleLevel(1);

        recyclerView.setAdapter(adapter);

        loadAndRefresh();

        return root;
    }

    private void refresh() {
        UpdateHelper updateHelper = new UpdateHelper(getContext());

        updateHelper.reloadMany(
                Grade.class,
                GradeCategory.class,
                Teacher.class,
                Subject.class,
                GradeComment.class)
                .isEmpty()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(empty -> {
                    if (empty) {
                        refreshLayout.setRefreshing(false);
                    } else {
                        loadAndRefresh();
                    }
                });
    }

    private void loadAndRefresh() {
        LibrusData data = LibrusData.getInstance(getActivity());
        Observable<EnrichedGrade> gradeObservable = data
                .findEnrichedGrades()
                .subscribeOn(Schedulers.io())
                .publish()
                .autoConnect(2);

        Single<Map<String, Collection<EnrichedGrade>>> gradesBySubjectId = gradeObservable.toMultimap(g -> g.subjectId());
        Single<List<FullSubject>> subjects = data.findFullSubjects().toList();
        Single<Map<FullSubject, Collection<EnrichedGrade>>> gradesBySubject = Single.zip(
                gradesBySubjectId,
                subjects,
                this::mapGradesToSubjects);
        gradesBySubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::displayGrades);

        gradeObservable.toList()
                .map(grades ->
                        new ReadAllMenuAction(
                                grades,
                                getContext(),
                                this::allGradesChanged))
                .map(Lists::newArrayList)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(actions -> actionsHandler.accept(actions));
    }

    private Map<FullSubject, Collection<EnrichedGrade>> mapGradesToSubjects(
            Map<String, Collection<EnrichedGrade>> gradesBySubjectId,
            List<FullSubject> subjects) {
        return StreamSupport.stream(subjects)
                .collect(Collectors.toMap(s -> s,
                        s -> Optional.ofNullable(gradesBySubjectId.get(s.id()))
                                .orElse(Collections.emptyList())
                ));
    }

    private void displayGrades(Map<FullSubject, Collection<EnrichedGrade>> mappedGrades) {

        adapter.clear();
        refreshLayout.setRefreshing(false);

        for (Map.Entry<FullSubject, Collection<EnrichedGrade>> entry : mappedGrades.entrySet()) {
            FullSubject s = entry.getKey();

            final GradeHeaderItem headerItem = new GradeHeaderItem(s, getContext());

            StreamSupport.stream(entry.getValue())
                    .sorted((g1, g2) -> g2.date().compareTo(g1.date()))
                    .forEach(grade ->
                            headerItem.addSubItem(new GradeItem(headerItem, grade)));

            getActivity().runOnUiThread(() -> adapter.addSection(headerItem, headerComparator));
        }
    }

    private void allGradesChanged() {
        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
    }

    @Override
    public boolean onItemClick(final int position) {
        AbstractFlexibleItem item = adapter.getItem(position);
        if (item instanceof GradeItem) {
            GradeItem gradeItem = (GradeItem) item;
            FullGrade fullGrade = LibrusData.getInstance(getActivity())
                    .blocking()
                    .makeFullGrade(gradeItem.getGrade());
            displayGradeDetails(fullGrade);
            reader.read(fullGrade);
            adapter.notifyItemChanged(position);

        } else //noinspection StatementWithEmptyBody
            if (item instanceof AverageItem) {
                //TODO
            }

        return false;
    }

    private void displayGradeDetails(FullGrade grade) {
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
    public void setMenuActionsHandler(java8.util.function.Consumer<List<? extends MenuAction>> actionsHandler) {
        this.actionsHandler = actionsHandler;
    }

    @Override
    public int getTitle() {
        return R.string.grades_view_title;
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_assignment_black_48dp;
    }
}
