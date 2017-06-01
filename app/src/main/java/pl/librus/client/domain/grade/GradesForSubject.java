package pl.librus.client.domain.grade;

import org.immutables.value.Value;

import java.util.List;
import java.util.Map;

import pl.librus.client.domain.subject.FullSubject;

/**
 * Created by szyme on 04.04.2017.
 */
@Value.Immutable
public interface GradesForSubject {
    @Value.Parameter
    FullSubject subject();

    @Value.Parameter
    List<EnrichedGrade> grades();

    public static GradesForSubject fromMapEntry(Map.Entry<FullSubject, List<EnrichedGrade>> entry) {
        return ImmutableGradesForSubject.of(entry.getKey(), entry.getValue());
    }
}
