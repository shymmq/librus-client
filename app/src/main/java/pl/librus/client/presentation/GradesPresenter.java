package pl.librus.client.presentation;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java8.util.Optional;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import pl.librus.client.MainActivityScope;
import pl.librus.client.R;
import pl.librus.client.data.LibrusData;
import pl.librus.client.data.Reader;
import pl.librus.client.data.UpdateHelper;
import pl.librus.client.domain.Teacher;
import pl.librus.client.domain.grade.BaseGrade;
import pl.librus.client.domain.grade.EnrichedGrade;
import pl.librus.client.domain.grade.FullGrade;
import pl.librus.client.domain.grade.Grade;
import pl.librus.client.domain.grade.GradeCategory;
import pl.librus.client.domain.grade.GradeComment;
import pl.librus.client.domain.subject.FullSubject;
import pl.librus.client.domain.subject.Subject;
import pl.librus.client.ui.MainActivityOps;
import pl.librus.client.ui.ReadAllMenuAction;
import pl.librus.client.ui.grades.GradesFragment;

/**
 * Created by robwys on 28/03/2017.
 */

@MainActivityScope
public class GradesPresenter extends MainFragmentPresenter {


    public static final int TITLE = R.string.grades_view_title;
    private final UpdateHelper updateHelper;

    private final LibrusData data;
    private final Context context;
    private final Reader reader;
    private final GradesFragment fragment;

    @Inject
    public GradesPresenter(UpdateHelper updateHelper, LibrusData data, MainActivityOps mainActivity, Context context, Reader reader) {
        super(mainActivity);
        this.updateHelper = updateHelper;
        this.data = data;
        this.context = context;
        this.reader = reader;
        this.fragment = new GradesFragment();
        fragment.setPresenter(this);
    }

    @Override
    public Fragment getFragment() {
        return fragment;
    }

    @Override
    public int getTitle() {
        return TITLE;
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_assignment_black_48dp;
    }

    @Override
    public int getOrder() {
        return 2;
    }

    public void refresh() {

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
                        fragment.setRefreshing(false);
                    } else {
                        loadAndRefresh();
                    }
                });
    }

    public void gradeClicked(int position, EnrichedGrade grade) {
        FullGrade fullGrade = data
                .blocking()
                .makeFullGrade(grade);
        reader.read(grade);
        fragment.updateGrade(position);
        fragment.displayGradeDetails(fullGrade);
    }

    public void loadAndRefresh() {
        Observable<EnrichedGrade> gradeObservable = data
                .findEnrichedGrades()
                .subscribeOn(Schedulers.io())
                .publish()
                .autoConnect(2);

        Single<Map<String, Collection<EnrichedGrade>>> gradesBySubjectId = gradeObservable.toMultimap(BaseGrade::subjectId);
        Single<List<FullSubject>> subjects = data.findFullSubjects().toList();
        Single<Map<FullSubject, Collection<EnrichedGrade>>> gradesBySubject = Single.zip(
                gradesBySubjectId,
                subjects,
                this::mapGradesToSubjects);
        gradesBySubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(fragment::displayGrades);

        gradeObservable.toList()
                .map(grades ->
                        new ReadAllMenuAction(
                                grades,
                                context,
                                fragment::updateGrades))
                .map(Lists::newArrayList)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mainActivity::displayMenuActions);
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
}
