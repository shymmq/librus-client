package pl.librus.client.domain.grade;

import com.google.common.base.Optional;

import org.immutables.value.Value;

import java.util.List;

import pl.librus.client.domain.Teacher;
import pl.librus.client.domain.subject.Subject;

/**
 * Created by robwys on 03/03/2017.
 */

@Value.Immutable
public abstract class FullGrade extends BaseGrade {

    public abstract FullGradeCategory category();

    public abstract List<GradeComment> comments();

    public abstract Optional<Teacher> addedBy();

    public abstract Subject subject();

    public enum GradeType {
        NORMAL, SEMESTER_PROPOSITION, SEMESTER, FINAL_PROPOSITION, FINAL
    }

    public GradeType type() {
        if (semesterPropositionType()) return GradeType.SEMESTER_PROPOSITION;
        else if (semesterType()) return GradeType.SEMESTER;
        else if (finalPropositionType()) return GradeType.FINAL_PROPOSITION;
        else if (finalType()) return GradeType.FINAL;
        else return GradeType.NORMAL;
    }

    public Optional<String> addedByName() {
        return addedBy().isPresent() ?
                addedBy().get().name() : Optional.absent();
    }
}
