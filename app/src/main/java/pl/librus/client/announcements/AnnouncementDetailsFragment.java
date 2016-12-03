package pl.librus.client.announcements;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import pl.librus.client.R;
import pl.librus.client.api.Announcement;
import pl.librus.client.ui.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnnouncementDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnnouncementDetailsFragment extends Fragment {
    private static final String ARG_ANNOUNCEMENT = "librus-client:AnnouncementDetailsFragment:announcement";
    private Announcement announcement;

    public AnnouncementDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param announcement Announcement to show
     * @return A new instance of fragment AnnouncementDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AnnouncementDetailsFragment newInstance(Announcement announcement) {
        AnnouncementDetailsFragment fragment = new AnnouncementDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ANNOUNCEMENT, announcement);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            announcement = (Announcement) getArguments().getSerializable(ARG_ANNOUNCEMENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_announcement_details, container, false);

        ((MainActivity) getActivity()).setBackArrow(true);

        TextView title = (TextView) root.findViewById(R.id.fragment_announcement_details_top_panel);
        TextView content = (TextView) root.findViewById(R.id.fragment_announcement_details_bottom_panel);
        TextView author = (TextView) root.findViewById(R.id.two_line_list_item_title);
        TextView date = (TextView) root.findViewById(R.id.two_line_list_item_content);
        View background = root.findViewById(R.id.fragment_announcement_details);
        View info = root.findViewById(R.id.fragment_announcement_details_info);
        title.setText(announcement.getSubject());
        content.setText(announcement.getContent());
        author.setText(announcement.getTeacher().getName());
        date.setText(announcement.getStartDate().toString("EEEE, d MMMM yyyy", new Locale("pl")));

        background.setTransitionName("announcement_background_" + announcement.getId());
        info.setTransitionName("announcement_info_" + announcement.getId());
        return root;
    }

}
