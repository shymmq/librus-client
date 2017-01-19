package pl.librus.client.timetable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.Payload;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;
import pl.librus.client.R;

class ProgressItem extends AbstractFlexibleItem<ProgressItem.ViewHolder> {

    private StatusEnum status = StatusEnum.MORE_TO_LOAD;

    public enum StatusEnum {
        MORE_TO_LOAD,    //Default = should have an empty Payload
        DISABLE_ENDLESS, //Endless is disabled because user has set limits
        NO_MORE_LOAD,    //Non-Empty payload = Payload.NO_MORE_LOAD
        ON_CANCEL,
        ON_ERROR
    }

    public StatusEnum getStatus() {
        return status;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.progress_item;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.progress_item, parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {
        holder.progressBar.setVisibility(View.VISIBLE);
    }

    class ViewHolder extends FlexibleViewHolder {
        View progressBar;

        public ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            progressBar = view.findViewById(R.id.progressBar4);
        }
    }
}