package pl.librus.client;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 2016-11-01. balbla
 */

class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder> {
    private List<Announcement> announcementList = new ArrayList<>();

    AnnouncementAdapter(List<Announcement> announcementList) {
        this.announcementList = announcementList;
    }

    @Override
    public AnnouncementAdapter.AnnouncementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.three_line_list_item, parent, false);
        return new AnnouncementViewHolder(v);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final AnnouncementViewHolder holder, int position) {
        holder.announcementSubject.setText(announcementList.get(position).getSubject());
        holder.announcementTeacherName.setText(announcementList.get(position).getTeacher().getName());
        holder.announcementContent.setText(announcementList.get(position).getContent());
        holder.announcementDate.setText(announcementList.get(position).getStartDate().toString("d MMM."));
    }

    @Override
    public int getItemCount() {
        return announcementList.size();
    }

    static class AnnouncementViewHolder extends RecyclerView.ViewHolder {
        private final View root;
        public RelativeLayout background;
        TextView announcementTeacherName, announcementSubject, announcementContent, announcementDate;
        Context context;

        AnnouncementViewHolder(View root) {
            super(root);
            context = root.getContext();
            this.root = root;
//            announcementTeacherPicture = (CircleImageView) root.findViewById(R.id.picture_announcement_item);
//            announcementTeacherName = (TextView) root.findViewById(R.id.announcementTeacherName);
//            announcementSubject = (TextView) root.findViewById(R.id.announcementSubject);
//            announcementContent = (TextView) root.findViewById(R.id.announcementContentShort);
//            background = (RelativeLayout) root.findViewById(R.id.background);
            announcementSubject = (TextView) root.findViewById(R.id.three_line_list_item_title);
            announcementTeacherName = (TextView) root.findViewById(R.id.three_line_list_item_first);
            announcementContent = (TextView) root.findViewById(R.id.three_line_list_item_second);
            announcementDate = (TextView) root.findViewById(R.id.three_line_list_item_date);
        }
    }
}
