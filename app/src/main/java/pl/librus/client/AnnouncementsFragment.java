package pl.librus.client;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                AnnouncementDetailsFragment fragment = AnnouncementDetailsFragment.newInstance(announcementList.get(position));
//                Transition transition =
//                fragment.setSharedElementEnterTransition(new ChangeBounds());
//                fragment.setSharedElementReturnTransition(new ChangeBounds());
                fragmentTransaction.addSharedElement(root.findViewById(R.id.three_line_list_item_title), "three_line_list_element_title");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.hide(AnnouncementsFragment.this);
                fragmentTransaction.add(((ViewGroup) getView().getParent()).getId(), fragment);
                fragmentTransaction.commit();
            }
        }));
        return root;
    }

}
