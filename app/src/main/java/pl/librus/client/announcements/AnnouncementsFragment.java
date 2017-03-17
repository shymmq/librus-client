package pl.librus.client.announcements;

import android.os.Bundle;
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

import com.google.common.collect.Ordering;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java8.util.stream.StreamSupport;
import pl.librus.client.R;
import pl.librus.client.api.LibrusData;
import pl.librus.client.api.Reader;
import pl.librus.client.datamodel.announcement.FullAnnouncement;
import pl.librus.client.ui.MainActivity;
import pl.librus.client.ui.MainFragment;

import static java8.util.stream.Collectors.toList;

public class AnnouncementsFragment extends MainFragment {

    private View root;

    public AnnouncementsFragment() {
        // Required empty public constructor
    }

    public static AnnouncementsFragment newInstance() {
        Bundle args = new Bundle();
        AnnouncementsFragment fragment = new AnnouncementsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_announcements, container, false);

        LibrusData.getInstance(getActivity())
                .findFullAnnouncements()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::displayList);
        return root;
    }

    private void displayList(List<? extends FullAnnouncement> announcements) {
        RecyclerView mRecyclerView = (RecyclerView) root.findViewById(R.id.recycler_announcements);
        Ordering<AnnouncementItem> ordering = Ordering.natural()
                .onResultOf(AnnouncementItem::getHeaderOrder)
                .compound(Ordering.natural()
                        .onResultOf(AnnouncementItem::getStartDate).reverse());

        List<AnnouncementItem> announcementItems = StreamSupport.stream(announcements)
                .map(a -> new AnnouncementItem(a, AnnouncementUtils.getHeaderOf(a, getContext())))
                .sorted(ordering)
                .collect(toList());

        final FlexibleAdapter<AnnouncementItem> adapter = new FlexibleAdapter<>(announcementItems);
        adapter.setDisplayHeadersAtStartUp(true);
        adapter.mItemClickListener = position -> {
            AnnouncementItem item = adapter.getItem(position);
            FullAnnouncement announcement = item.getAnnouncement();

            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            AnnouncementDetailsFragment announcementDetailsFragment = AnnouncementDetailsFragment.newInstance(announcement);

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
            ft.addToBackStack(null);
            ft.commit();
            return false;
        };
        adapter.mItemLongClickListener = position -> {
            new Reader(getContext()).modify(adapter.getItem(position).getAnnouncement(), false);
            adapter.notifyItemChanged(position);
        };
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
        ((MainActivity) getActivity()).setBackArrow(false);
    }

    @Override
    public int getTitle() {
        return R.string.announcements_view_title;
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_announcement_black_48dp;

    }
}
