package pl.librus.client.grades;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.flexibleadapter.items.IHeader;

/**
 * Created by szyme on 22.01.2017.
 */
abstract class GradeEntryItem<VH extends RecyclerView.ViewHolder, H extends IHeader> extends AbstractSectionableItem<VH, H> implements Comparable<GradeEntryItem> {
    private final int priority;
    private final GradeEntry entry;

    GradeEntryItem(H header, int priority, GradeEntry entry) {
        super(header);

        this.priority = priority;
        this.entry = entry;
    }

    @Override
    public int compareTo(@NonNull GradeEntryItem o) {
        int priorityComp = Integer.compare(priority, o.priority);
        return (priorityComp != 0) ? priorityComp : entry.compareTo(o.entry);

    }
}
