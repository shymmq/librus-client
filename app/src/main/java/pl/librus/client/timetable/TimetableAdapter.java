package pl.librus.client.timetable;

import android.support.annotation.Nullable;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;

/**
 * Created by szyme on 21.01.2017.
 */

class TimetableAdapter extends FlexibleAdapter<IFlexible> {
    OnLoadMoreListener onLoadMoreListener = new OnLoadMoreListener() {
        @Override
        public void onLoadMore() {

        }
    };

    TimetableAdapter(@Nullable List<IFlexible> items) {
        super(items);
    }

    interface OnLoadMoreListener {
        void onLoadMore();
    }
}
