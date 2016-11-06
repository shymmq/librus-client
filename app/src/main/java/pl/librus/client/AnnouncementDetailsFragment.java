package pl.librus.client;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        TextView title = (TextView) root.findViewById(R.id.fragment_announcement_details_title);
        title.setText(announcement.getSubject());
        return root;
    }

}
