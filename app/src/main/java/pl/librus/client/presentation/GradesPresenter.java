package pl.librus.client.presentation;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import java8.util.function.Function;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import pl.librus.client.MainActivityScope;
import pl.librus.client.R;
import pl.librus.client.data.LibrusData;
import pl.librus.client.data.Reader;
import pl.librus.client.data.UpdateHelper;
import pl.librus.client.domain.Identifiable;
import pl.librus.client.domain.LibrusColor;
import pl.librus.client.domain.Teacher;
import pl.librus.client.domain.grade.EnrichedGrade;
import pl.librus.client.domain.grade.Grade;
import pl.librus.client.domain.grade.GradeCategory;
import pl.librus.client.domain.grade.GradeComment;
import pl.librus.client.domain.grade.GradesForSubject;
import pl.librus.client.domain.grade.ImmutableGradesForSubject;
import pl.librus.client.domain.subject.FullSubject;
import pl.librus.client.domain.subject.Subject;
import pl.librus.client.ui.MainActivityOps;
import pl.librus.client.ui.MenuAction;
import pl.librus.client.ui.ReadAllMenuAction;
import pl.librus.client.ui.grades.GradesFragment;
import pl.librus.client.ui.grades.GradesView;

/**
 * Created by robwys on 28/03/2017.
 */

@MainActivityScope
public class GradesPresenter extends ReloadablePresenter<List<GradesForSubject>, GradesView> {

    public static final int TITLE = R.string.grades_view_title;

    private final LibrusData data;
    private final Context context;
    private final Reader reader;

    @Inject
    protected GradesPresenter(UpdateHelper updateHelper,
                              LibrusData data,
                              Context context,
                              Reader reader,
                              MainActivityOps mainActivity,
                              ErrorHandler errorHandler) {
        super(mainActivity, updateHelper, errorHandler);
        this.data = data;
        this.context = context;
        this.reader = reader;
    }

    @Override
    public Fragment getFragment() {
        return new GradesFragment();
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

    @Override
    protected Single<List<GradesForSubject>> fetchData() {
        return Single.zip(
                data.findFullSubjects().toList(),
                data.findEnrichedGrades().toList(),
                this::mapGradesToSubjects);
    }

    @Override
    protected List<MenuAction> getMenuActions(List<GradesForSubject> data) {
        List<EnrichedGrade> grades = StreamSupport.stream(data)
                .flatMap(gfs -> StreamSupport.stream(gfs.grades()))
                .collect(Collectors.toList());

        ReadAllMenuAction reloadAll = new ReadAllMenuAction(grades, context, this);

        return ImmutableList.<MenuAction>builder()
                .add(reloadAll)
                .addAll(super.getMenuActions(data))
                .build();
    }

    public void gradeClicked(int position, EnrichedGrade grade) {
        reader.read(grade);
        view.updateGrade(position);
        mainActivity.updateMenu();

        subscription = data.makeFullGrade(grade)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::displayGradeDetails, errorHandler);
    }

    private List<GradesForSubject> mapGradesToSubjects(List<FullSubject> subjects, List<EnrichedGrade> grades) {
        Map<String, FullSubject> subjectMap = Maps.uniqueIndex(subjects, FullSubject::id);

        Function<EnrichedGrade, FullSubject> gradeKey = grade -> subjectMap.get(grade.subjectId());

        Map<FullSubject, List<EnrichedGrade>> gradeMap = StreamSupport.stream(grades)
                .collect(Collectors.groupingBy(gradeKey));
        return StreamSupport.stream(gradeMap.keySet())
                .map(subject -> ImmutableGradesForSubject.of(subject, gradeMap.get(subject)))
                .collect(Collectors.toList());
    }

    @Override
    protected Set<Class<? extends Identifiable>> dependentEntities() {
        return Sets.newHashSet(
                Grade.class,
                GradeCategory.class,
                Teacher.class,
                Subject.class,
                GradeComment.class,
                LibrusColor.class);
    }
}
