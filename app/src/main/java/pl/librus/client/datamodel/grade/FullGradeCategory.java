package pl.librus.client.datamodel.grade;

import com.google.common.base.Optional;

import org.immutables.value.Value;

import pl.librus.client.datamodel.LibrusColor;

/**
 * Created by robwys on 02/03/2017.
 */

@Value.Immutable
public abstract class FullGradeCategory extends BaseGradeCategory {

    public abstract Optional<LibrusColor> color();

}
