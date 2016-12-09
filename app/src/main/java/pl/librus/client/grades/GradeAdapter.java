package pl.librus.client.grades;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;

import java.util.List;

import pl.librus.client.R;
import pl.librus.client.api.Grade;

/**
 * Created by szyme on 09.12.2016. librus-client
 */

class GradeAdapter extends ExpandableRecyclerAdapter<GradeCategory, Grade, GradeCategoryViewHolder, GradeViewHolder> {

    private LayoutInflater inflater;

    /**
     * Primary constructor. Sets up {@link #mParentList} and {@link #mFlatItemList}.
     * <p>
     * Any changes to {@link #mParentList} should be made on the original instance, and notified via
     * {@link #notifyParentInserted(int)}
     * {@link #notifyParentRemoved(int)}
     * {@link #notifyParentChanged(int)}
     * {@link #notifyParentRangeInserted(int, int)}
     * {@link #notifyChildInserted(int, int)}
     * {@link #notifyChildRemoved(int, int)}
     * {@link #notifyChildChanged(int, int)}
     * methods and not the notify methods of RecyclerView.Adapter.
     *
     * @param parentList List of all parents to be displayed in the RecyclerView that this
     *                   adapter is linked to
     * @param context    Context
     */
    GradeAdapter(@NonNull List<GradeCategory> parentList, Context context) {
        super(parentList);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public GradeCategoryViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View root = inflater.inflate(R.layout.grade_category_item, parentViewGroup, false);
        return new GradeCategoryViewHolder(root);
    }

    @NonNull
    @Override
    public GradeViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View root = inflater.inflate(R.layout.grade_category_item, childViewGroup, false);
        return new GradeViewHolder(root);
    }


    @Override
    public void onBindParentViewHolder(@NonNull GradeCategoryViewHolder parentViewHolder, int parentPosition, @NonNull GradeCategory parent) {
        parentViewHolder.bind(parent);
    }

    @Override
    public void onBindChildViewHolder(@NonNull GradeViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull Grade child) {
        childViewHolder.bind(child);
    }
}
