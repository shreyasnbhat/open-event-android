package org.fossasia.openevent.fragments;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.SessionsListAdapter;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.extras.SocialLink;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.events.BookmarkChangedEvent;
import org.fossasia.openevent.utils.ISO8601Date;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;
import io.realm.RealmResults;
import timber.log.Timber;

/**
 * Created by harshita30 on 9/3/17.
 */

public class AboutFragment extends BaseFragment {

    @BindView(R.id.welcomeMessage)
    TextView welcomeMessage;
    @BindView(R.id.event_descrption)
    TextView event_descrption;
    @BindView(R.id.event_timing_details)
    TextView event_timing;
    @BindView(R.id.organiser_description)
    TextView organiser_description;
    @BindView(R.id.carddemo)
    CardView cardView;
    @BindView(R.id.item_description_img)
    ImageView mDescriptionImg;
    @BindView(R.id.readmore)
    TextView readMore;
    @BindView(R.id.readless)
    TextView readLess;
    @BindView(R.id.img_twitter)
    ImageView img_twitter;
    @BindView(R.id.img_facebook)
    ImageView img_facebook;
    @BindView(R.id.img_github)
    ImageView img_github;
    @BindView(R.id.img_linkedin)
    ImageView img_linkedin;
    @BindView(R.id.event_venue_details)
    TextView venue_details;
    @BindView(R.id.list_bookmarks)
    RecyclerView bookmarksRecyclerView;

    final private String SEARCH = "org.fossasia.openevent.searchText";

    private String searchText = "";
    private SearchView searchView;

    private static final int bookmarkedSessionList =3;
    private SessionsListAdapter sessionsListAdapter;
    private RealmResults<Session> bookmarksResult;
    private List<Session> mSessions = new ArrayList<>();

    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();
    private Event event;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = super.onCreateView(inflater, container, savedInstanceState);

        bookmarksRecyclerView.setVisibility(View.VISIBLE);
        sessionsListAdapter = new SessionsListAdapter(getContext(), mSessions, bookmarkedSessionList);
        sessionsListAdapter.setBookmarkView(true);
        bookmarksRecyclerView.setAdapter(sessionsListAdapter);
        bookmarksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        if (savedInstanceState != null && savedInstanceState.getString(SEARCH) != null) {
            searchText = savedInstanceState.getString(SEARCH);
        }

        return view;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_about;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        event = realmRepo.getEvent();
        event.addChangeListener(realmModel -> loadEvent());
    }

    private void loadEvent() {
        if(event == null)
            return;

        String date = ISO8601Date.getDateFromDateString(event.getStartTime(), event.getEndTime());

        welcomeMessage.setText(getText(R.string.welcome_message));
        organiser_description.setText(Html.fromHtml(event.getOrganizerDescription()));
        event_descrption.setText(Html.fromHtml(event.getDescription()));
        venue_details.setText(event.getLocationName());
        event_timing.setText(date);
        mDescriptionImg.setOnClickListener(v -> collapseExpandTextView());
        readMore.setOnClickListener(v -> {
            organiser_description.setMaxLines(Integer.MAX_VALUE);
            readMore.setVisibility(View.GONE);
            readLess.setVisibility(View.VISIBLE);
        });
        readLess.setOnClickListener(v -> {
            organiser_description.setMaxLines(4);
            readLess.setVisibility(View.GONE);
            readMore.setVisibility(View.VISIBLE);
        });

        final List<SocialLink> socialLinks = event.getSocialLinks();
        img_twitter.setOnClickListener(v -> setUpCustomTab(socialLinks.get(2).getLink()));

        img_facebook.setOnClickListener(v -> setUpCustomTab(socialLinks.get(3).getLink()));

        img_github.setOnClickListener(v -> setUpCustomTab(socialLinks.get(7).getLink()));

        img_linkedin.setOnClickListener(v -> setUpCustomTab(socialLinks.get(8).getLink()));

    }

    @TargetApi(16)
    void collapseExpandTextView() {
        if (event_descrption.getVisibility() == View.GONE) {
            // it's collapsed - expand it
            event_descrption.setVisibility(View.VISIBLE);
            mDescriptionImg.setImageResource(R.drawable.ic_expand_less_black_24dp);
        } else {
            // it's expanded - collapse it
            event_descrption.setVisibility(View.GONE);
            mDescriptionImg.setImageResource(R.drawable.ic_expand_more_black_24dp);
        }

        ObjectAnimator animation = ObjectAnimator.ofInt(event_descrption, "maxLines", event_descrption.getMaxLines());
        animation.setDuration(200).start();
    }

    private void setUpCustomTab(String url) {

        Uri uri = Uri.parse(url);

        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();

        intentBuilder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.color_primary));
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(getContext(), R.color.color_primary_dark));

        intentBuilder.setStartAnimations(getContext(), R.anim.slide_in_right, R.anim.slide_out_left);
        intentBuilder.setExitAnimations(getContext(), android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);

        CustomTabsIntent customTabsIntent = intentBuilder.build();

        customTabsIntent.launchUrl(getActivity(), uri);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_home, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager)getContext(). getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search_home).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default
    }

    @Subscribe
    public void onBookmarksChanged(BookmarkChangedEvent bookmarkChangedEvent) {
        Timber.d("Bookmarks changed");
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private List<Session> filter(List<Session> sessions, String query) {
        String lowerCaseQuery = query.toLowerCase(Locale.getDefault());
        final List<Session> filteredTracksList = new ArrayList<>();
        for (Session session : sessions) {
            final String text = session.getTitle().toLowerCase(Locale.getDefault());
            if (text.contains(lowerCaseQuery)) {
                filteredTracksList.add(session);
            }
        }
        return filteredTracksList;
    }

    private void handleVisibility() {
        if (!mSessions.isEmpty()) {
            bookmarksRecyclerView.setVisibility(View.VISIBLE);
        } else {
            bookmarksRecyclerView.setVisibility(View.GONE);
        }
    }

    private void loadData() {
        bookmarksResult = realmRepo.getBookMarkedSessions();
        bookmarksResult.removeAllChangeListeners();
        bookmarksResult.addChangeListener((bookmarked, orderedCollectionChangeSet) -> {
            mSessions.clear();
            mSessions.addAll(bookmarked);

            sessionsListAdapter.notifyDataSetChanged();

            handleVisibility();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        OpenEventApp.getEventBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        OpenEventApp.getEventBus().unregister(this);
        if(bookmarksResult != null)
            bookmarksResult.removeAllChangeListeners();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        if (searchView != null) {
            bundle.putString(SEARCH, searchText);
        }
        super.onSaveInstanceState(bundle);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        // Remove listeners to fix memory leak
        if(searchView != null) searchView.setOnQueryTextListener(null);
    }
}