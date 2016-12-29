package pl.librus.client.announcements;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_announcements, container, false);
        RecyclerView mRecyclerView = (RecyclerView) root.findViewById(R.id.recycler_announcements);

        LibrusData data = (LibrusData) getArguments().getSerializable(ARG_DATA);
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

//        FlexibleAdapter.OnItemClickListener onClick = new ;
        final FlexibleAdapter<AnnouncementItem> adapter = new FlexibleAdapter<>(listItems);
        adapter.setDisplayHeadersAtStartUp(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
//        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(getContext(), new RecyclerViewItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                Object item = adapter.getItem(position);
//                if (item instanceof AnnouncementItem) {
//                    FragmentManager fragmentManager = getFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    Announcement announcement = (Announcement) item;
//                    AnnouncementDetailsFragment fragment = AnnouncementDetailsFragment.newInstance(announcement, teacherMap != null ? teacherMap.get(announcement.getAuthorId()) : null);
//                    RelativeLayout background = (RelativeLayout) view.findViewById(R.id.three_line_list_item_background);
//
//                    TransitionInflater transitionInflater = TransitionInflater.from(getContext());
//                    Transition t = new Fade();
//                    Transition details_enter = transitionInflater.inflateTransition(R.transition.details_enter);
//                    Transition details_exit = transitionInflater.inflateTransition(R.transition.details_exit);
//
//                    if (debug) {
//                        details_enter.setDuration(3000);
//                        details_exit.setDuration(3000);
//                        t.setDuration(3000);
//                    }
//
//                    fragment.setSharedElementEnterTransition(details_enter);
//                    fragment.setSharedElementReturnTransition(details_exit);
//                    setSharedElementEnterTransition(details_enter);
//                    setSharedElementReturnTransition(details_exit);
//
//                    //TODO extend Fade to allow other starting/ending values
//
//                    fragment.setExitTransition(t);
//                    fragment.setEnterTransition(t);
//                    fragment.setReturnTransition(t);
//                    fragment.setReenterTransition(t);
//
//                    setEnterTransition(t);
//                    setExitTransition(t);
//                    setReturnTransition(t);
//                    setReenterTransition(t);
//
//                    background.setTransitionName("announcement_background_" + (announcement).getId());
//                    fragmentTransaction.addSharedElement(background, background.getTransitionName());
//                    fragmentTransaction.addToBackStack(null).commit();
//                    @SuppressWarnings("ConstantConditions") ViewGroup parent = (ViewGroup) getView().getParent();
//                    fragmentTransaction.replace(parent.getId(), fragment);
//                }
//            }
//
//        }));
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
