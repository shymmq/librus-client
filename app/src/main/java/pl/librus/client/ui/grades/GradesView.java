package pl.librus.client.ui.grades;

import java.util.List;

import pl.librus.client.domain.grade.FullGrade;
import pl.librus.client.domain.grade.GradesForSubject;
import pl.librus.client.ui.MainView;

/**
 * Created by szyme on 04.04.2017.
 */

public interface GradesView extends MainView<List<GradesForSubject>> {
    void updateGrades();

    void updateGrade(int position);

    void displayGradeDetails(FullGrade grade);

    void setRefreshing(boolean b);
}
