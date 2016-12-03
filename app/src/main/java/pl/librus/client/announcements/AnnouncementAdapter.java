package pl.librus.client.announcements;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import pl.librus.client.R;
import pl.librus.client.api.Announcement;

/**
 * Created by Adam on 2016-11-01. balbla
 */

class AnnouncementAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "librus-client-log";
    private ArrayList<ArrayList<Announcement>> sections = new ArrayList<ArrayList<Announcement>>();
    private Map<Integer, String> titles = new ArrayMap<>();
    private List<Object> positions = new ArrayList<>();


    AnnouncementAdapter(List<Announcement> announcementList) {
        titles.put(0, "Dzisiaj");
        titles.put(1, "Wczoraj");
        titles.put(2, "Ten tydzień");
        titles.put(3, "Ten miesiąc");
        titles.put(4, "Starsze");
        titles.put(5, "Nieprzeczytane");
        for (int i = 0; i < titles.size(); i++) {
            sections.add(i, new ArrayList<Announcement>());
        }
        Collections.sort(announcementList);
        for (Announcement a : announcementList) {
            LocalDate date = a.getStartDate();
            if (a.isUnread()) {
                a.setCategory(5);
                sections.get(5).add(a);
            } else if (!date.isBefore(LocalDate.now())) {
                a.setCategory(0);
                sections.get(0).add(a);
            } else if (!date.isBefore(LocalDate.now().minusDays(1))) {
                a.setCategory(1);
                sections.get(1).add(a);
            } else if (!date.isBefore(LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY))) {
                a.setCategory(2);
                sections.get(2).add(a);
            } else if (!date.isBefore(LocalDate.now().withDayOfMonth(1))) {
                a.setCategory(3);
                sections.get(3).add(a);
            } else {
                a.setCategory(4);
                sections.get(4).add(a);
            }
        }
        if (sections.get(5).size() > 0) {
            positions.add(titles.get(5));
            Collections.sort(sections.get(5));
            for (Announcement a : sections.get(5)) {
                positions.add(a);
            }
        } else {
            Log.d(TAG, "AnnouncementAdapter: Section " + titles.get(5) + " is empty");
        }
        for (int i = 0; i < sections.size() - 1; i++) {
            List<Announcement> section = sections.get(i);
            Collections.sort(section);
            if (section.size() > 0) {
                positions.add(titles.get(i));
                for (Announcement a : section) {
                    positions.add(a);
                }
            } else {
                Log.d(TAG, "AnnouncementAdapter: Section " + titles.get(i) + " is empty");
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case 0:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.three_line_list_item, parent, false);
                return new AnnouncementViewHolder(v);
            case 1:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_subheader, parent, false);
                return new SubheaderViewHolder(v);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                AnnouncementViewHolder holder0 = (AnnouncementViewHolder) holder;
                Announcement announcement = (Announcement) positions.get(position);
                holder0.announcementSubject.setText(announcement.getSubject());
                holder0.background.setTransitionName("announcement_background_" + announcement.getId());
                holder0.announcementTeacherName.setText(announcement.getTeacher().getName());
                holder0.announcementContent.setText(announcement.getContent());
                holder0.announcementDate.setText(announcement.getStartDate().toString("d MMM."));
                if (announcement.isUnread()) {
                    holder0.announcementSubject.setTypeface(holder0.announcementSubject.getTypeface(), Typeface.BOLD);
                } else {
                    holder0.announcementSubject.setTypeface(null, Typeface.NORMAL);
                }
                break;
            case 1:
                SubheaderViewHolder holder1 = (SubheaderViewHolder) holder;
                holder1.sectionTitle.setText((CharSequence) positions.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return positions.get(position) instanceof Announcement ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return positions.size();
    }

    public List<Object> getPositions() {
        return positions;
    }

    private static class AnnouncementViewHolder extends RecyclerView.ViewHolder {
        public final RelativeLayout background;
        final TextView announcementTeacherName;
        final TextView announcementSubject;
        final TextView announcementContent;
        final TextView announcementDate;
        final Context context;
        private final View root;

        AnnouncementViewHolder(View root) {
            super(root);
            context = root.getContext();
            this.root = root;
//            announcementTeacherPicture = (CircleImageView) root.findViewById(R.id.picture_announcement_item);
//            announcementTeacherName = (TextView) root.findViewById(R.id.announcementTeacherName);
//            announcementSubject = (TextView) root.findViewById(R.id.announcementSubject);
//            announcementContent = (TextView) root.findViewById(R.id.announcementContentShort);
            background = (RelativeLayout) root.findViewById(R.id.three_line_list_item_background);
            announcementSubject = (TextView) root.findViewById(R.id.three_line_list_item_title);
            announcementTeacherName = (TextView) root.findViewById(R.id.three_line_list_item_first);
            announcementContent = (TextView) root.findViewById(R.id.three_line_list_item_second);
            announcementDate = (TextView) root.findViewById(R.id.three_line_list_item_date);
        }
    }

    private static class SubheaderViewHolder extends RecyclerView.ViewHolder {
        final TextView sectionTitle;

        public SubheaderViewHolder(View root) {
            super(root);
            sectionTitle = (TextView) root.findViewById(R.id.list_subheader_title);
        }
    }
}
