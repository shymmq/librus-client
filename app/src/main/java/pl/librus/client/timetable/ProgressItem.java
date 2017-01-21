package pl.librus.client.timetable;

import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;
import pl.librus.client.R;

class ProgressItem extends AbstractFlexibleItem<ProgressItem.ViewHolder> {

    static final int IDLE = 0;
    static final int LOADING = 1;
    private int status = IDLE;

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.progress_item;
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.progress_item, parent, false), (TimetableAdapter) adapter);
    }

    @Override
    public void bindViewHolder(final FlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {
        TransitionManager.beginDelayedTransition((ViewGroup) holder.itemView);
        if (this.status == IDLE) {
            holder.button.setVisibility(View.VISIBLE);
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TimetableAdapter) adapter).onLoadMoreListener.onLoadMore();
                }
            });
            holder.progressBar.setVisibility(View.GONE);
        } else if (status == LOADING) {
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.button.setVisibility(View.GONE);
        }


//        LibrusUtils.log("Progress item .bindViewHolder()");
    }

    class ViewHolder extends FlexibleViewHolder {
        View progressBar;
        Button button;

        ViewHolder(View view, TimetableAdapter adapter) {
            super(view, adapter);
            progressBar = view.findViewById(R.id.progress_item_progressbar);
            button = (Button) view.findViewById(R.id.progress_item_button);
        }
    }
}