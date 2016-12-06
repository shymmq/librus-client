package pl.librus.client.announcements;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.io.Serializable;
import java.util.List;

import pl.librus.client.R;
import pl.librus.client.api.Announcement;
import pl.librus.client.api.LibrusCache;
import pl.librus.client.ui.MainActivity;
import pl.librus.client.ui.MainFragment;
import pl.librus.client.ui.RecyclerViewItemClickListener;

public class AnnouncementsFragment extends MainFragment {
    private final String TAG = "librus-client-log";
    private final boolean debug = false;

    public AnnouncementsFragment() {
        // Required empty public constructor
    }

    public static AnnouncementsFragment newInstance(LibrusCache cache) {
        Bundle args = new Bundle();
        Serializable announcements = (Serializable) cache.getAnnouncements();
        args.putSerializable("data", announcements);
        AnnouncementsFragment fragment = new AnnouncementsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_announcements, container, false);
        List<Announcement> announcementList = (List<Announcement>) getArguments().getSerializable("data");
        RecyclerView mRecyclerView = (RecyclerView) root.findViewById(R.id.recycler_announcements);
        assert announcementList != null;
        final AnnouncementAdapter adapter = new AnnouncementAdapter(announcementList, getContext());
        mRecyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(getContext(), mRecyclerView, new RecyclerViewItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Object item = adapter.getPositions().get(position);
                if (item instanceof Announcement) {
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    AnnouncementDetailsFragment fragment = AnnouncementDetailsFragment.newInstance((Announcement) item);
                    RelativeLayout background = (RelativeLayout) view.findViewById(R.id.three_line_list_item_background);

                    TransitionInflater transitionInflater = TransitionInflater.from(getContext());
                    Transition t = new Fade();
                    Transition details_enter = transitionInflater.inflateTransition(R.transition.details_enter);
                    Transition details_exit = transitionInflater.inflateTransition(R.transition.details_exit);

                    if (debug) {
                        details_enter.setDuration(3000);
                        details_exit.setDuration(3000);
                        t.setDuration(3000);
                    }

                    fragment.setSharedElementEnterTransition(details_enter);
                    fragment.setSharedElementReturnTransition(details_exit);
                    setSharedElementEnterTransition(details_enter);
                    setSharedElementReturnTransition(details_exit);

                    //TODO extend Fade to allow other starting/ending values

                    fragment.setExitTransition(t);
                    fragment.setEnterTransition(t);
                    fragment.setReturnTransition(t);
                    fragment.setReenterTransition(t);

                    setEnterTransition(t);
                    setExitTransition(t);
                    setReturnTransition(t);
                    setReenterTransition(t);

                    background.setTransitionName("announcement_background_" + ((Announcement) item).getId());
                    fragmentTransaction.addSharedElement(background, background.getTransitionName());
                    fragmentTransaction.addToBackStack(null).commit();
                    fragmentTransaction.replace(((ViewGroup) getView().getParent()).getId(), fragment);
                }
            }

        }));
        ((MainActivity) getActivity()).setBackArrow(false);
        return root;
    }

    @Override
    public void refresh(LibrusCache cache) {
        Log.d(TAG, "AnnouncementsFragment update()");

    }
}
