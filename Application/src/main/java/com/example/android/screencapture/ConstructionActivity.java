package com.example.android.screencapture;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ConstructionActivity extends FragmentActivity implements ActionBar.TabListener {

        /**
         * The {@link android.support.v4.view.PagerAdapter} that will provide
         * fragments for each of the sections. We use a
         * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
         * will keep every loaded fragment in memory. If this becomes too memory
         * intensive, it may be best to switch to a
         * {@link android.support.v4.app.FragmentStatePagerAdapter}.
         */
        SectionsPagerAdapter mSectionsPagerAdapter;

        /**
         * The {@link ViewPager} that will host the section contents.
         */
        ViewPager mViewPager;

        /**
         * Create the activity. Sets up an {@link android.app.ActionBar} with tabs, and then configures the
         * {@link ViewPager} contained inside R.layout.activity_main.
         *
         * <p>A {@link SectionsPagerAdapter} will be instantiated to hold the different pages of
         * fragments that are to be displayed. A
         * {@link android.support.v4.view.ViewPager.SimpleOnPageChangeListener} will also be configured
         * to receive callbacks when the user swipes between pages in the ViewPager.
         *
         * @param savedInstanceState
         */
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the UI from res/layout/activity_main.xml
            setContentView(R.layout.construction_main);

            // Set up the action bar. The navigation mode is set to NAVIGATION_MODE_TABS, which will
            // cause the ActionBar to render a set of tabs. Note that these tabs are *not* rendered
            // by the ViewPager; additional logic is lower in this file to synchronize the ViewPager
            // state with the tab state. (See mViewPager.setOnPageChangeListener() and onTabSelected().)
            // BEGIN_INCLUDE (set_navigation_mode)
            final ActionBar actionBar = getActionBar();
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            // END_INCLUDE (set_navigation_mode)

            // BEGIN_INCLUDE (setup_view_pager)
            // Create the adapter that will return a fragment for each of the three primary sections
            // of the app.
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.pager);
            mViewPager.setAdapter(mSectionsPagerAdapter);
            // END_INCLUDE (setup_view_pager)

            // When swiping between different sections, select the corresponding tab. We can also use
            // ActionBar.Tab#select() to do this if we have a reference to the Tab.
            // BEGIN_INCLUDE (page_change_listener)
            mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    actionBar.setSelectedNavigationItem(position);
                }
            });
            // END_INCLUDE (page_change_listener)

            // BEGIN_INCLUDE (add_tabs)
            // For each of the sections in the app, add a tab to the action bar.
            for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
                // Create a tab with text corresponding to the page title defined by the adapter. Also
                // specify this Activity object, which implements the TabListener interface, as the
                // callback (listener) for when this tab is selected.
                actionBar.addTab(
                        actionBar.newTab()
                                .setText(mSectionsPagerAdapter.getPageTitle(i))
                                .setTabListener(this));
            }
            // END_INCLUDE (add_tabs)
        }

        /**
         * Update {@link ViewPager} after a tab has been selected in the ActionBar.
         *
         * @param tab Tab that was selected.
         * @param fragmentTransaction A {@link android.app.FragmentTransaction} for queuing fragment operations to
         *                            execute once this method returns. This FragmentTransaction does
         *                            not support being added to the back stack.
         */
        // BEGIN_INCLUDE (on_tab_selected)
        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            // When the given tab is selected, tell the ViewPager to switch to the corresponding page.
            mViewPager.setCurrentItem(tab.getPosition());
        }
        // END_INCLUDE (on_tab_selected)

        /**
         * Unused. Required for {@link android.app.ActionBar.TabListener}.
         */
        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        }

        /**
         * Unused. Required for {@link android.app.ActionBar.TabListener}.
         */
        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        }

        // BEGIN_INCLUDE (fragment_pager_adapter)
        /**
         * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
         * one of the sections/tabs/pages. This provides the data for the {@link ViewPager}.
         */
        public class SectionsPagerAdapter extends FragmentPagerAdapter {
            // END_INCLUDE (fragment_pager_adapter)

            public SectionsPagerAdapter(FragmentManager fm) {
                super(fm);
            }

            // BEGIN_INCLUDE (fragment_pager_adapter_getitem)
            /**
             * Get fragment corresponding to a specific position. This will be used to populate the
             * contents of the {@link ViewPager}.
             *
             * @param position Position to fetch fragment for.
             * @return Fragment for specified position.
             */
            @Override
            public Fragment getItem(int position) {
                // getItem is called to instantiate the fragment for the given page.
                // Return a DummySectionFragment (defined as a static inner class
                // below) with the page number as its lone argument.
                AppListFragment fragment = new AppListFragment();
                fragment.setStatus(position);

                return (Fragment)fragment;
            }
            // END_INCLUDE (fragment_pager_adapter_getitem)

            // BEGIN_INCLUDE (fragment_pager_adapter_getcount)
            /**
             * Get number of pages the {@link ViewPager} should render.
             *
             * @return Number of fragments to be rendered as pages.
             */
            @Override
            public int getCount() {
                // Show 3 total pages.
                return 3;
            }
            // END_INCLUDE (fragment_pager_adapter_getcount)

            // BEGIN_INCLUDE (fragment_pager_adapter_getpagetitle)
            /**
             * Get title for each of the pages. This will be displayed on each of the tabs.
             *
             * @param position Page to fetch title for.
             * @return Title for specified page.
             */
            @Override
            public CharSequence getPageTitle(int position) {
                Locale l = Locale.getDefault();
                switch (position) {
                    case 0:
                        return "未開始".toUpperCase(l);
                    case 1:
                        return "進行中".toUpperCase(l);
                    case 2:
                        return "完成中".toUpperCase(l);
                }
                return null;
            }
            // END_INCLUDE (fragment_pager_adapter_getpagetitle)
        }

    /**
     * A custom Loader that loads all of the installed applications.
     */
    public static class AppListLoader extends android.support.v4.content.AsyncTaskLoader<List<String>> {
        final PackageManager mPm;
        private int mStatus;

        public AppListLoader(Context context) {
            super(context);

            // Retrieve the package manager for later use; note we don't
            // use 'context' directly but instead the save global application
            // context returned by getContext().
            mPm = getContext().getPackageManager();
        }

        public void setStatus(int status) {
            mStatus = status;
        }

        /**
         * This is where the bulk of our work is done.  This function is
         * called in a background thread and should generate a new set of
         * data to be published by the loader.
         */
        @Override public List<String> loadInBackground() {

            List<String> mCons = new ArrayList<>();

            Connection mConnection = null;
            try {
                mConnection = ERPConnectionFactory.GetConnection();
                Statement stmt = mConnection.createStatement();

                String qry =              "SELECT     DescriptionCN, ConstructionApply_Id, ApplyNo "
                        + "FROM         ConstructionApply WHERE ProgressStatus = " + mStatus;

                ResultSet rs = stmt.executeQuery(qry);

                ArrayList<String> strings = new ArrayList<>();
                while (rs.next()) {
                    mCons.add(rs.getString("DescriptionCN"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Create corresponding array of entries and load their labels.
            List<String> entries = new ArrayList<>(mCons.size());
            for (int i=0; i<mCons.size(); i++) {
                entries.add(mCons.get(i));
            }
//            final Context context = getContext();
//
//            // Create corresponding array of entries and load their labels.
//            List<AppEntry> entries = new ArrayList<>(apps.size());
//            for (int i=0; i<apps.size(); i++) {
//                AppEntry entry = new AppEntry(this, apps.get(i));
//                entry.loadLabel(context);
//                entries.add(entry);
//            }

            // Sort the list.
            Collections.sort(entries, ALPHA_COMPARATOR);

            // Done!
            return entries;
        }

        /**
         * Handles a request to start the Loader.
         */
        @Override protected void onStartLoading() {
            forceLoad();
        }

        /**
         * Handles a request to stop the Loader.
         */
        @Override protected void onStopLoading() {
            // Attempt to cancel the current load task if possible.
            cancelLoad();
        }

        /**
         * Handles a request to cancel a load.
         */
        @Override public void onCanceled(List<String> apps) {
            super.onCanceled(apps);
        }

        /**
         * Handles a request to completely reset the Loader.
         */
        @Override protected void onReset() {
            super.onReset();

            // Ensure the loader is stopped
            onStopLoading();

        }

    }

    public static class AppListAdapter extends FuzzyArrayAdapter<String> {
        private final LayoutInflater mInflater;

        public AppListAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_2);
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setData(List<String> data) {
            clear();
            if (data != null) {
                addAll(data);
            }
        }

        /**
         * Populate new items in the list.
         */
        @Override public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                view = mInflater.inflate(R.layout.list_item_icon_text, parent, false);
            } else {
                view = convertView;
            }

            String item = getItem(position);
            ((TextView)view.findViewById(R.id.text)).setText(item);

            return view;
        }
    }

    public static class AppListFragment extends ListFragment
            implements SearchView.OnQueryTextListener, SearchView.OnCloseListener,
            android.support.v4.app.LoaderManager.LoaderCallbacks<List<String>> {

        // This is the Adapter being used to display the list's data.
        AppListAdapter mAdapter;

        // The SearchView for doing filtering.
        SearchView mSearchView;

        // If non-null, this is the current filter the user has provided.
        String mCurFilter;
        private int mStatus;

        public void setStatus(int status) {
            mStatus = status;
        }

        @Override public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            // Give some text to display if there is no data.  In a real
            // application this would come from a resource.
            setEmptyText("No applications");

            // We have a menu item to show in action bar.
            setHasOptionsMenu(true);

            // Create an empty adapter we will use to display the loaded data.
            mAdapter = new AppListAdapter(getActivity());
            setListAdapter(mAdapter);

            // Start out with a progress indicator.
            setListShown(false);

            // Prepare the loader.  Either re-connect with an existing one,
            // or start a new one.
            getLoaderManager().initLoader(0, null, this);
        }

        public static class MySearchView extends SearchView {
            public MySearchView(Context context) {
                super(context);
            }

            // The normal SearchView doesn't clear its search text when
            // collapsed, so we will do this for it.
            @Override
            public void onActionViewCollapsed() {
                setQuery("", false);
                super.onActionViewCollapsed();
            }
        }

        @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Place an action bar item for searching.
            MenuItem item = menu.add("Search");
            item.setIcon(android.R.drawable.ic_menu_search);
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM
                    | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
            mSearchView = new AppListFragment.MySearchView(getActivity());
            mSearchView.setOnQueryTextListener(this);
            mSearchView.setOnCloseListener(this);
            mSearchView.setIconifiedByDefault(true);
            item.setActionView(mSearchView);
        }

        @Override public boolean onQueryTextChange(String newText) {
            // Called when the action bar search text has changed.  Since this
            // is a simple array adapter, we can just have it do the filtering.
            mCurFilter = !TextUtils.isEmpty(newText) ? newText : null;

            mAdapter.getFilter().filter(mCurFilter);
            return true;
        }

        @Override public boolean onQueryTextSubmit(String query) {
            // Don't care about this.
            return true;
        }

        @Override
        public boolean onClose() {
            if (!TextUtils.isEmpty(mSearchView.getQuery())) {
                mSearchView.setQuery(null, true);
            }
            return true;
        }

        @Override public void onListItemClick(ListView l, View v, int position, long id) {
            // Insert desired behavior here.
            Log.i("LoaderCustom", "Item clicked: " + id);
        }

        @Override public android.support.v4.content.Loader<List<String>> onCreateLoader(int id, Bundle args) {
            // This is called when a new Loader needs to be created.  This
            // sample only has one Loader with no arguments, so it is simple.
            AppListLoader loader = new AppListLoader(getActivity());
            loader.setStatus(mStatus);
            return loader;
        }

        @Override public void onLoadFinished(android.support.v4.content.Loader<List<String>> loader, List<String> data) {
            // Set the new data in the adapter.
            mAdapter.setData(data);

            // The list should now be shown.
            if (isResumed()) {
                setListShown(true);
            } else {
                setListShownNoAnimation(true);
            }
        }

        @Override public void onLoaderReset(android.support.v4.content.Loader<List<String>> loader) {
            // Clear the data in the adapter.
            mAdapter.setData(null);
        }
    }

    public static final Comparator<String> ALPHA_COMPARATOR = new Comparator<String>() {
        private final Collator sCollator = Collator.getInstance();
        @Override
        public int compare(String object1, String object2) {
            return sCollator.compare(object1, object2);
        }
    };
}
