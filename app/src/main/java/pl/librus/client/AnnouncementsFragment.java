package pl.librus.client;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementsFragment extends Fragment {
    private final String TAG = "librus-client-log";
    private List<Announcement> announcementList = new ArrayList<>();
    private RecyclerView mRecyclerView;

    public AnnouncementsFragment() {
        // Required empty public constructor
    }

    public static AnnouncementsFragment newInstance(List<Announcement> announcementList) {
        Bundle args = new Bundle();
        args.putSerializable("data", (Serializable) announcementList);
        AnnouncementsFragment fragment = new AnnouncementsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_announcements, container, false);
        announcementList = (List<Announcement>) getArguments().getSerializable("data");
        mRecyclerView = (RecyclerView) root.findViewById(R.id.recycler_announcements);
        assert announcementList != null;
        mRecyclerView.setAdapter(new AnnouncementAdapter(announcementList));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(getContext(), mRecyclerView, new RecyclerViewItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Announcement announcement = announcementList.get(position);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                AnnouncementDetailsFragment fragment = AnnouncementDetailsFragment.newInstance(announcement);

                Transition changeBounds = TransitionInflater.from(getContext()).inflateTransition(R.transition.change_bounds);
                fragment.setSharedElementEnterTransition(changeBounds);
                fragment.setSharedElementReturnTransition(changeBounds);
                setSharedElementEnterTransition(changeBounds);
                setSharedElementReturnTransition(changeBounds);

                Transition transition = new Fade();
                fragment.setEnterTransition(transition);
                fragment.setExitTransition(transition);
                fragment.setReenterTransition(transition);
                fragment.setReturnTransition(transition);

                setEnterTransition(transition);
                setExitTransition(transition);
                setReenterTransition(transition);
                setReturnTransition(transition);

                TextView title = (TextView) view.findViewById(R.id.three_line_list_item_title);
                fragmentTransaction.addSharedElement(title, title.getTransitionName());
//                fragmentTransaction.addSharedElement(root.findViewById(R.id.three_line_list_item_background), "three_line_list_element_background");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(((ViewGroup) getView().getParent()).getId(), fragment);
                fragmentTransaction.commit();
            }
        }));
        return root;
    }

}
