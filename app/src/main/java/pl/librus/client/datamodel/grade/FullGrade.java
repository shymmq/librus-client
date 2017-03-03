package pl.librus.client.datamodel.grade;

import org.immutables.value.Value;

import java.util.List;

import pl.librus.client.datamodel.subject.Subject;
import pl.librus.client.datamodel.Teacher;

/**
 * Created by robwys on 03/03/2017.
 */

@Value.Immutable
public abstract class FullGrade extends BaseGrade {

    public abstract FullGradeCategory category();

    public abstract List<GradeComment> comments();

    public abstract Teacher addedBy();

    public abstract Subject subject();
}
