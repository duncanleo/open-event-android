package org.fossasia.openevent.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.ScheduleSessionDetailActivity;
import org.fossasia.openevent.activities.SessionDetailActivity;
import org.fossasia.openevent.adapters.ScheduleSessionsListAdapter;
import org.fossasia.openevent.adapters.SessionsListAdapter;
import org.fossasia.openevent.api.Urls;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.IntentStrings;
import org.fossasia.openevent.utils.RecyclerItemClickListener;
import org.fossasia.openevent.utils.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by duncanleo on 1/2/16.
 */
public class SessionFragment extends Fragment implements SearchView.OnQueryTextListener {
    final private String SEARCH = "searchText";
    private String searchText = "";
    private SearchView searchView;

    private RecyclerView sessionsRecyclerView;
    private ScheduleSessionsListAdapter sessionsListAdapter;
    private List<Session> data;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        OpenEventApp.getEventBus().register(this);
        super.onCreate(savedInstanceState);
        data = DbSingleton.getInstance().getSessionList();
//        data = getArguments().getParcelableArrayList("data");

        if (savedInstanceState != null && savedInstanceState.getString(SEARCH) != null) {
            searchText = savedInstanceState.getString(SEARCH);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        if (isAdded()) {
            if (searchView != null) {
                bundle.putString(SEARCH, searchText);
            }
        }
        super.onSaveInstanceState(bundle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_session, container, false);
        sessionsRecyclerView = (RecyclerView)v.findViewById(R.id.sessionRecyclerView);
        sessionsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sessionsListAdapter = new ScheduleSessionsListAdapter(data);
        sessionsRecyclerView.setAdapter(sessionsListAdapter);
        sessionsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        sessionsRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity().getResources()));
        sessionsRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), ScheduleSessionDetailActivity.class);
                intent.putExtra(IntentStrings.SESSION, data.get(position).getTitle());
                startActivity(intent);
            }
        }));
        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_tracks_url:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, Urls.WEB_APP_URL_BASIC + Urls.TRACKS);
                intent.putExtra(Intent.EXTRA_SUBJECT, R.string.share_links);
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_links)));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_tracks, menu);
        MenuItem item = menu.findItem(R.id.action_search_tracks);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        if (searchText != null) {
            searchView.setQuery(searchText, false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (!TextUtils.isEmpty(query)) {
            searchText = query;
            sessionsListAdapter.getFilter().filter(searchText);
        } else {
            sessionsListAdapter.refresh();
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
}
