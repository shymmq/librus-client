package pl.librus.client.announcements;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
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
import eu.davidea.flexibleadapter.items.IFlexible;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java8.util.stream.StreamSupport;
import pl.librus.client.R;
import pl.librus.client.api.LibrusData;
import pl.librus.client.datamodel.announcement.FullAnnouncement;
import pl.librus.client.ui.BaseFragment;
import pl.librus.client.ui.MainActivity;

import static java8.util.stream.Collectors.toList;

public class AnnouncementsFragment extends BaseFragment {

    private View root;
    private SwipeRefreshLayout refreshLayout;

    public AnnouncementsFragment() {
        // Required empty public constructor
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
        refreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.fragment_announcements_refresh_layout);

        Ordering<AnnouncementItem> ordering = Ordering.natural()
                .onResultOf(AnnouncementItem::getHeaderOrder)
                .compound(Ordering.natural()
                        .onResultOf(AnnouncementItem::getStartDate).reverse());

        List<IFlexible> announcementItems = StreamSupport.stream(announcements)
                .map(a -> new AnnouncementItem(a, AnnouncementUtils.getHeaderOf(a, getContext())))
                .sorted(ordering)
                .collect(toList());

        final FlexibleAdapter<IFlexible> adapter = new FlexibleAdapter<>(announcementItems);
        adapter.setDisplayHeadersAtStartUp(true);
        adapter.mItemClickListener = position -> {
            IFlexible item = adapter.getItem(position);
            if (!(item instanceof AnnouncementItem)) return false;

            AnnouncementItem announcementItem = (AnnouncementItem) item;
            FullAnnouncement announcement = announcementItem.getAnnouncement();

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
            ft.addSharedElement(announcementItem.getBackgroundView(), announcementItem.getBackgroundView().getTransitionName());
            ft.addToBackStack(null);
            ft.commit();

            return true;
        };
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);

        refreshLayout.setColorSchemeResources(R.color.md_blue_grey_400, R.color.md_blue_grey_500, R.color.md_blue_grey_600);
        refreshLayout.setOnRefreshListener(this::refresh);

        ((MainActivity) getActivity()).setBackArrow(false);
    }

    private void refresh() {
        refreshLayout.setRefreshing(false);
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
