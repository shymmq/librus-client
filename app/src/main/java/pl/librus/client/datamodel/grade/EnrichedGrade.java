package pl.librus.client.datamodel.grade;

import org.immutables.value.Value;

@Value.Immutable
public abstract class EnrichedGrade extends BaseGrade {

    public abstract FullGradeCategory category();
}
