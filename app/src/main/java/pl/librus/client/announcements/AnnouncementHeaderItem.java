package pl.librus.client.announcements;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractHeaderItem;
import eu.davidea.viewholders.FlexibleViewHolder;
import pl.librus.client.R;

/**
 * Created by szyme on 28.12.2016. librus-client
 */

class AnnouncementHeaderItem extends AbstractHeaderItem<AnnouncementHeaderItem.ViewHolder> {

    private String title;
    private int order;
    private boolean dismissable = false;

    AnnouncementHeaderItem(String title, int order) {
        this.title = title;
        this.order = order;
    }

    AnnouncementHeaderItem(String title, int order, boolean dismissable) {
        this.title = title;
        this.order = order;
        this.dismissable = dismissable;
    }

    @Override
    public ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.list_subheader, parent, false), adapter);
    }

    @Override
    public void bindViewHolder(final FlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {
        holder.title.setText(title);
        holder.title.setTypeface(holder.title.getTypeface(), Typeface.BOLD);
        final AnnouncementHeaderItem header = this;
//        if (dismissable) {
//            holder.done.setVisibility(View.VISIBLE);
//            holder.done.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    List children = adapter.getSectionItems(header);
//                    adapter.removeRange(adapter.getGlobalPositionOf(header), children.size() + 1);
//                    for (Object c : children) {
//                        AnnouncementItem a = (AnnouncementItem) c;
//                        a.setRead(true);
//                        Reader.read(Reader.TYPE_ANNOUNCEMENT, a.getAnnouncement().id(), v.getContext());
//                        adapter.addItemToSection(a, AnnouncementUtils.getHeaderOf(a.getAnnouncement(), v.getContext()), AnnouncementUtils.getItemComparator());
//                    }
//                }
//            });
//        } else {
//            holder.done.setVisibility(View.GONE);
//        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnnouncementHeaderItem that = (AnnouncementHeaderItem) o;

        return order == that.order;

    }

    @Override
    public int hashCode() {
        return order;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.list_subheader;
    }

    class ViewHolder extends FlexibleViewHolder {
        TextView title;

        ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            title = (TextView) view.findViewById(R.id.list_subheader_title);
        }
    }

    public int getOrder() {
        return order;
    }
}
