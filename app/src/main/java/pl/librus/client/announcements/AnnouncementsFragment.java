package pl.librus.client.announcements;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.AutoTransition;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import pl.librus.client.R;
import pl.librus.client.api.Announcement;
import pl.librus.client.api.LibrusData;
import pl.librus.client.api.Reader;
import pl.librus.client.api.Teacher;
import pl.librus.client.ui.MainActivity;
import pl.librus.client.ui.MainFragment;

public class AnnouncementsFragment extends Fragment implements MainFragment {
    private static final String ARG_ANNOUNCEMENTS = "AnnouncementsFragment:announcements";
    private static final String ARG_TEACHERS = "AnnouncementsFragment:teachers";
    private static final String ARG_DATA = "AnnouncementsFragment:data";
    private final String TAG = "librus-client-log";
    private final boolean debug = false;
    private OnSetupCompleteListener listener;

    public AnnouncementsFragment() {
        // Required empty public constructor
    }

    public static AnnouncementsFragment newInstance(LibrusData data) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATA, data);
        AnnouncementsFragment fragment = new AnnouncementsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_announcements, container, false);
        Log.d(TAG, "AnnouncementsFragments onCreateView()");
        postponeEnterTransition();
        RecyclerView mRecyclerView = (RecyclerView) root.findViewById(R.id.recycler_announcements);

        final LibrusData data = (LibrusData) getArguments().getSerializable(ARG_DATA);
        assert data != null;

        List<Announcement> announcementList = data.getAnnouncements();
        Collections.sort(announcementList);
        List<AnnouncementItem> listItems = new ArrayList<>();

        AnnouncementHeaderItem unread = new AnnouncementHeaderItem("Nieprzeczytane", 0);
        AnnouncementHeaderItem today = new AnnouncementHeaderItem("Dzisiaj", 1);
        AnnouncementHeaderItem yesterday = new AnnouncementHeaderItem("Wczoraj", 2);
        AnnouncementHeaderItem thisWeek = new AnnouncementHeaderItem("Ten tydzień", 3);
        AnnouncementHeaderItem thisMonth = new AnnouncementHeaderItem("Ten miesiąc", 4);
        AnnouncementHeaderItem older = new AnnouncementHeaderItem("Starsze", 5);

        for (Announcement a : announcementList) {
            LocalDate date = a.getStartDate();
            if (!Reader.isRead(Reader.TYPE_ANNOUNCEMENT, a.getId(), getContext()))
                listItems.add(new AnnouncementItem(a, data, unread));
            else if (!date.isBefore(LocalDate.now()))
                listItems.add(new AnnouncementItem(a, data, today));
            else if (!date.isBefore(LocalDate.now().minusDays(1)))
                listItems.add(new AnnouncementItem(a, data, yesterday));
            else if (!date.isBefore(LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY)))
                listItems.add(new AnnouncementItem(a, data, thisWeek));
            else if (!date.isBefore(LocalDate.now().withDayOfMonth(1)))
                listItems.add(new AnnouncementItem(a, data, thisMonth));
            else
                listItems.add(new AnnouncementItem(a, data, older));
        }

        final FlexibleAdapter<AnnouncementItem> adapter = new FlexibleAdapter<>(listItems);
        adapter.setDisplayHeadersAtStartUp(true);
        adapter.mItemClickListener = new FlexibleAdapter.OnItemClickListener() {
            @Override
            public boolean onItemClick(int position) {
                AnnouncementItem item = adapter.getItem(position);
                Announcement announcement = item.getAnnouncement();
                Teacher teacher = data.getTeacherMap().get(announcement.getAuthorId());

                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                AnnouncementDetailsFragment announcementDetailsFragment = AnnouncementDetailsFragment.newInstance(announcement, teacher);

                TransitionInflater transitionInflater = TransitionInflater.from(getContext());
                Transition details_enter = transitionInflater.inflateTransition(R.transition.details_enter);
                Transition details_exit = transitionInflater.inflateTransition(R.transition.details_exit);

                setSharedElementEnterTransition(details_enter);
                setSharedElementReturnTransition(details_exit);
                setExitTransition(new Fade());
                announcementDetailsFragment.setSharedElementEnterTransition(details_enter);
                announcementDetailsFragment.setSharedElementReturnTransition(details_exit);

                ft.replace(R.id.content_main, announcementDetailsFragment, "Announcement details transition");
                ft.addSharedElement(item.getBackgroundView(), item.getBackgroundView().getTransitionName());
                ft.addToBackStack("aaas");
                ft.commit();
                return false;
            }
        };
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startPostponedEnterTransition();
            }
        }, 50);
        ((MainActivity) getActivity()).setBackArrow(false);
        if (listener != null) listener.onSetupComplete();

        return root;
    }

    @Override
    public void refresh(LibrusData cache) {
        Log.d(TAG, "AnnouncementsFragment update()");

    }

    @Override
    public void setOnSetupCompleteListener(OnSetupCompleteListener listener) {
        this.listener = listener;
    }
}
