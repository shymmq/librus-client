package pl.librus.client.grades;

import android.support.annotation.Nullable;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;

/**
 * Created by szyme on 22.01.2017.
 */

public class GradeAdapter extends FlexibleAdapter<AbstractFlexibleItem> {
    public GradeAdapter(@Nullable List<AbstractFlexibleItem> items) {
        super(items);
    }
}
