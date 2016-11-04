package pl.librus.client;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Adam on 2016-11-01.
 */

class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder> {
    private List<Announcement> announcementList = new ArrayList<>();

    AnnouncementAdapter(List<Announcement> announcementList) {
        this.announcementList = announcementList;
    }

    @Override
    public AnnouncementAdapter.AnnouncementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_announcement_item_alt, parent, false);
        return new AnnouncementViewHolder(v);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(AnnouncementViewHolder holder, int position) {
        holder.announcementSubject.setText(announcementList.get(position).getSubject());
        holder.announcementContent.setText(announcementList.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    static class AnnouncementViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout background;
        CircleImageView announcementTeacherPicture;
        TextView announcementTeacherName, announcementSubject, announcementContent;

        AnnouncementViewHolder(View root) {
            super(root);
            announcementTeacherPicture = (CircleImageView) root.findViewById(R.id.picture_announcement_item);
            announcementTeacherName = (TextView) root.findViewById(R.id.announcementTeacherName);
            announcementSubject = (TextView) root.findViewById(R.id.announcementSubject);
            announcementContent = (TextView) root.findViewById(R.id.announcementContentShort);
            background = (LinearLayout) root.findViewById(R.id.background);
        }
    }
}
