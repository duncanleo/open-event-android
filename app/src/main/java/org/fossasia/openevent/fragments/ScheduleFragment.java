package org.fossasia.openevent.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.ScheduleSessionsListAdapter;
import org.fossasia.openevent.api.Urls;

import timber.log.Timber;

/**
 * Created by duncanleo on 4/2/16.
 */
public class ScheduleFragment extends Fragment implements SearchView.OnQueryTextListener {
    final private String SEARCH = "searchText";
    private String searchText = "";
    private SearchView searchView;

    public final static String SCHEDULE_TAB_POSITION = "SCHEDULE_TAB_POSITION";
    private TabLayout scheduleTabLayout;
    private ViewPager scheduleViewPager;
    private ScheduleViewPagerAdapter viewPagerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        OpenEventApp.getEventBus().register(this);
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getString(SEARCH) != null) {
            searchText = savedInstanceState.getString(SEARCH);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_schedule, container, false);
        scheduleTabLayout = (TabLayout)v.findViewById(R.id.schedule_tab_layout);
        scheduleViewPager = (ViewPager)v.findViewById(R.id.schedule_view_pager);
        viewPagerAdapter = new ScheduleViewPagerAdapter(getActivity().getSupportFragmentManager());
        scheduleViewPager.setAdapter(viewPagerAdapter);
        scheduleTabLayout.setupWithViewPager(scheduleViewPager);
        return v;
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

    private class ScheduleViewPagerAdapter extends FragmentPagerAdapter {
        private String[] titles;
        private Fragment currentFragment;
        private SparseArray<Fragment> registeredFragments = new SparseArray<>();

        public ScheduleViewPagerAdapter(FragmentManager fm) {
            super(fm);
            titles = getResources().getStringArray(R.array.days);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new SessionFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(SCHEDULE_TAB_POSITION, position);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment)super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter_schedule:
                System.out.println("Filter i shall!");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_schedule, menu);
        MenuItem item = menu.findItem(R.id.action_search_schedule);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        if (searchText != null) {
            searchView.setQuery(searchText, false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        SessionFragment sessionFragment = (SessionFragment)viewPagerAdapter.getRegisteredFragment(scheduleViewPager.getCurrentItem());
        if (sessionFragment == null) {
            Timber.d("SessionFragment is null!");
            return true;
        }
        ScheduleSessionsListAdapter sessionsListAdapter = sessionFragment.getSessionsListAdapter();
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
