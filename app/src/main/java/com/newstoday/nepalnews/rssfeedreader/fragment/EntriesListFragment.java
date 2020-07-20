/*
  spaRSS
  <p/>
  Copyright (c) 2015-2016 Arnaud Renaud-Goud
  Copyright (c) 2012-2015 Frederic Julian
  <p/>
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  <p/>
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  <p/>
  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.newstoday.nepalnews.rssfeedreader.fragment;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.snackbar.Snackbar;
import com.newstoday.nepalnews.MainApplication;
import com.newstoday.nepalnews.R;
import com.newstoday.nepalnews.rssfeedreader.Constants;
import com.newstoday.nepalnews.rssfeedreader.activity.EditFeedsListActivity;
import com.newstoday.nepalnews.rssfeedreader.activity.GeneralPrefsActivity;
import com.newstoday.nepalnews.rssfeedreader.adapter.EntriesCursorAdapter;
import com.newstoday.nepalnews.rssfeedreader.provider.FeedData;
import com.newstoday.nepalnews.rssfeedreader.provider.FeedData.EntryColumns;
import com.newstoday.nepalnews.rssfeedreader.provider.FeedDataContentProvider;
import com.newstoday.nepalnews.rssfeedreader.service.FetcherService;
import com.newstoday.nepalnews.rssfeedreader.utils.PrefUtils;
import com.newstoday.nepalnews.rssfeedreader.utils.UiUtils;

import java.util.ArrayList;
import java.util.Date;

public class EntriesListFragment extends SwipeRefreshListFragment implements ViewTreeObserver.OnScrollChangedListener {

    private static final String STATE_CURRENT_URI = "STATE_CURRENT_URI";
    private static final String STATE_ORIGINAL_URI = "STATE_ORIGINAL_URI";
    private static final String STATE_SHOW_FEED_INFO = "STATE_SHOW_FEED_INFO";
    private static final String STATE_LIST_DISPLAY_DATE = "STATE_LIST_DISPLAY_DATE";

    private static final int ENTRIES_LOADER_ID = 1;
    private static final int NEW_ENTRIES_NUMBER_LOADER_ID = 2;

    private SwipeRefreshLayout mySwipeRefreshLayout;
    private final OnSharedPreferenceChangeListener mPrefListener = new OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (PrefUtils.SHOW_READ.equals(key)) {
                getLoaderManager().restartLoader(ENTRIES_LOADER_ID, null, mEntriesLoader);
            } else if (PrefUtils.IS_REFRESHING.equals(key)) {
                refreshSwipeProgress();
            }
        }
    };
    private Cursor mJustMarkedAsReadEntries;
    private Button mRefreshListBtn;
    private Uri mUri, mOriginalUri;
    private boolean mShowFeedInfo = false;
    private EntriesCursorAdapter mEntriesCursorAdapter;
    private ListView mListView;
    private long mListDisplayDate = new Date().getTime();
    private final LoaderManager.LoaderCallbacks<Cursor> mEntriesLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String entriesOrder = PrefUtils.getBoolean(PrefUtils.DISPLAY_OLDEST_FIRST, false) ? Constants.DB_ASC : Constants.DB_DESC;
            String where = "(" + EntryColumns.FETCH_DATE + Constants.DB_IS_NULL + Constants.DB_OR + EntryColumns.FETCH_DATE + "<=" + mListDisplayDate + ')';
            if (!FeedData.shouldShowReadEntries(mUri)) {
                where += Constants.DB_AND + EntryColumns.WHERE_UNREAD;
            }
            CursorLoader cursorLoader = new CursorLoader(getActivity(), mUri, null, where, null, EntryColumns.DATE + entriesOrder);
            cursorLoader.setUpdateThrottle(150);
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mEntriesCursorAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mEntriesCursorAdapter.swapCursor(Constants.EMPTY_CURSOR);
        }
    };
    private Menu menu;
    private int mNewEntriesNumber, mOldUnreadEntriesNumber = -1;
    private boolean mAutoRefreshDisplayDate = false;
    private final LoaderManager.LoaderCallbacks<Cursor> mEntriesNumberLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            CursorLoader cursorLoader = new CursorLoader(getActivity(), mUri, new String[]{"SUM(" + EntryColumns.FETCH_DATE + '>' + mListDisplayDate + ")", "SUM(" + EntryColumns.FETCH_DATE + "<=" + mListDisplayDate + Constants.DB_AND + EntryColumns.WHERE_UNREAD + ")"}, null, null, null);
            cursorLoader.setUpdateThrottle(150);
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            data.moveToFirst();
            mNewEntriesNumber = data.getInt(0);
            mOldUnreadEntriesNumber = data.getInt(1);

            if (mAutoRefreshDisplayDate && mNewEntriesNumber != 0 && mOldUnreadEntriesNumber == 0) {
                mListDisplayDate = new Date().getTime();
                restartLoaders();
            } else {
                refreshUI();
            }


            mAutoRefreshDisplayDate = false;
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);


        if (savedInstanceState != null) {
            mUri = savedInstanceState.getParcelable(STATE_CURRENT_URI);
            mOriginalUri = savedInstanceState.getParcelable(STATE_ORIGINAL_URI);
            mShowFeedInfo = savedInstanceState.getBoolean(STATE_SHOW_FEED_INFO);
            mListDisplayDate = savedInstanceState.getLong(STATE_LIST_DISPLAY_DATE);

            mEntriesCursorAdapter = new EntriesCursorAdapter(getActivity(), mUri, Constants.EMPTY_CURSOR, mShowFeedInfo);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mListView.getViewTreeObserver().addOnScrollChangedListener(this);
        refreshSwipeProgress();
        PrefUtils.registerOnPrefChangeListener(mPrefListener);

        if (mUri != null) {
            // If the list is empty when we are going back here, try with the last display date
            if (mNewEntriesNumber != 0 && mOldUnreadEntriesNumber == 0) {
                mListDisplayDate = new Date().getTime();
            } else {
                mAutoRefreshDisplayDate = true; // We will try to update the list after if necessary
            }

            restartLoaders();
        }
    }

    @Override
    public void onScrollChanged() {
        if (mListView.getFirstVisiblePosition() == 0) {
            mySwipeRefreshLayout.setEnabled(true);
        } else {
            mySwipeRefreshLayout.setEnabled(false);
        }
    }

    @Override
    public void inflateView(LayoutInflater inflater, ViewGroup container) {
        View rootView = inflater.inflate(R.layout.feed_fragment_entry_list, container, true);

        if (mEntriesCursorAdapter != null) {
            setListAdapter(mEntriesCursorAdapter);
        }

        mySwipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh);
        mySwipeRefreshLayout.setOnRefreshListener(
                this::startRefresh);

        mListView = rootView.findViewById(android.R.id.list);
        mListView.setOnTouchListener(new SwipeGestureListener(mListView.getContext()));

        if (PrefUtils.getBoolean(PrefUtils.DISPLAY_TIP, true)) {
            final TextView header = new TextView(mListView.getContext());
            header.setMinimumHeight(UiUtils.dpToPixel(70));
            int footerPadding = UiUtils.dpToPixel(10);
            header.setPadding(footerPadding, footerPadding, footerPadding, footerPadding);
            header.setText(R.string.tip_sentence);
            header.setGravity(Gravity.CENTER_VERTICAL);
            header.setCompoundDrawablePadding(UiUtils.dpToPixel(5));
            header.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_info_outline, 0, R.drawable.ic_cancel, 0);
            header.setClickable(true);
            header.setOnClickListener(view -> {
                mListView.removeHeaderView(header);
                PrefUtils.putBoolean(PrefUtils.DISPLAY_TIP, false);
            });
            mListView.addHeaderView(header);
        }

        UiUtils.addEmptyFooterView(mListView, 90);

        mRefreshListBtn = rootView.findViewById(R.id.refreshListBtn);
        mRefreshListBtn.setBackgroundResource(R.drawable.bg_refresh_list_button_selector);
        mRefreshListBtn.setOnClickListener(view -> {
            mNewEntriesNumber = 0;
            mListDisplayDate = new Date().getTime();
            refreshUI();
            if (mUri != null) {
                restartLoaders();
            }
        });


        disableSwipe();

    }

    @Override
    public void onStop() {
        PrefUtils.unregisterOnPrefChangeListener(mPrefListener);
        if (mJustMarkedAsReadEntries != null && !mJustMarkedAsReadEntries.isClosed()) {
            mJustMarkedAsReadEntries.close();
        }
        refreshUI();
        super.onStop();
        mListView.getViewTreeObserver().removeOnScrollChangedListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_URI, mUri);
        outState.putParcelable(STATE_ORIGINAL_URI, mOriginalUri);
        outState.putBoolean(STATE_SHOW_FEED_INFO, mShowFeedInfo);
        outState.putLong(STATE_LIST_DISPLAY_DATE, mListDisplayDate);
        refreshUI();
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRefresh() {
        startRefresh();
    }


    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        if (id >= 0) { // should not happen, but I had a crash with this on PlayStore...
            startActivity(new Intent(Intent.ACTION_VIEW, ContentUris.withAppendedId(mUri, id)));

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        menu.clear(); // This is needed to remove a bug on Android 4.0.3
        inflater.inflate(R.menu.new_entry_list, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        if (EntryColumns.isSearchUri(mUri)) {
            searchItem.expandActionView();
            // Without that, it just does not work
            searchView.post(() -> {
                searchView.setQuery(mUri.getLastPathSegment(), false);
                searchView.clearFocus();
            });

        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    setData(mOriginalUri, true);
                } else {
                    setData(EntryColumns.SEARCH_URI(newText), true, true);
                }
                return false;
            }
        });
        searchView.setOnCloseListener(() -> {
            setData(mOriginalUri, true);
            return false;
        });

        if (!PrefUtils.getBoolean(PrefUtils.SHOW_READ, true)) {
            menu.findItem(R.id.menu_unread).setIcon(R.drawable.nav_read_white);
        } else {
            menu.findItem(R.id.menu_unread).setIcon(R.drawable.nav_unread_white);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_unread:
                if (!PrefUtils.getBoolean(PrefUtils.SHOW_READ, true)) {
                    Toast.makeText(getActivity(), "All Items", Toast.LENGTH_SHORT).show();
                    PrefUtils.putBoolean(PrefUtils.SHOW_READ, true);
                    menu.findItem(R.id.menu_unread).setIcon(R.drawable.nav_unread_white);
                    return true;

                } else {
                    Toast.makeText(getActivity(), "Unread Items Only", Toast.LENGTH_SHORT).show();
                    PrefUtils.putBoolean(PrefUtils.SHOW_READ, false);
                    menu.findItem(R.id.menu_unread).setIcon(R.drawable.nav_read_white);
                }
                return true;

            case R.id.edit_feeds: {
                startActivity(new Intent(getActivity(), EditFeedsListActivity.class));
                return true;
            }
            case R.id.settings: {
                startActivity(new Intent(getActivity(), GeneralPrefsActivity.class));
                return true;
            }

            case R.id.menu_all_read: {
                if (mEntriesCursorAdapter != null) {
                    markAllRead();
                    // If we are on "all items" uri, we can remove the notification here
                    if (EntryColumns.CONTENT_URI.equals(mUri) && Constants.NOTIF_MGR != null) {
                        Constants.NOTIF_MGR.cancel(0);
                    }
                }
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void markAllRead() {
        if (mEntriesCursorAdapter != null) {
            Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.coordinator_layout), R.string.marked_as_read, Snackbar.LENGTH_LONG)
                    .setActionTextColor(ContextCompat.getColor(getActivity(), R.color.light_primary_color))
                    .setAction(R.string.undo, v -> new Thread() {
                        @Override
                        public void run() {
                            if (mJustMarkedAsReadEntries != null && !mJustMarkedAsReadEntries.isClosed()) {
                                ArrayList<Integer> ids = new ArrayList<>();
                                while (mJustMarkedAsReadEntries.moveToNext()) {
                                    ids.add(mJustMarkedAsReadEntries.getInt(0));
                                }
                                ContentResolver cr = MainApplication.getContext().getContentResolver();
                                String where = BaseColumns._ID + " IN (" + TextUtils.join(",", ids) + ')';
                                cr.update(EntryColumns.CONTENT_URI, FeedData.getUnreadContentValues(), where, null);

                                mJustMarkedAsReadEntries.close();
                            }
                        }
                    }.start());
            snackbar.getView().setBackgroundResource(R.color.material_grey_900);
            snackbar.show();

            new Thread() {
                @Override
                public void run() {
                    ContentResolver cr = MainApplication.getContext().getContentResolver();
                    String where = EntryColumns.WHERE_UNREAD + Constants.DB_AND + '(' + EntryColumns.FETCH_DATE + Constants.DB_IS_NULL + Constants.DB_OR + EntryColumns.FETCH_DATE + "<=" + mListDisplayDate + ')';
                    if (mJustMarkedAsReadEntries != null && !mJustMarkedAsReadEntries.isClosed()) {
                        mJustMarkedAsReadEntries.close();
                    }
                    mJustMarkedAsReadEntries = cr.query(mUri, new String[]{BaseColumns._ID}, where, null, null);
                    cr.update(mUri, FeedData.getReadContentValues(), where, null);
                }
            }.start();
        }
    }


    private void startRefresh() {
        if (!PrefUtils.getBoolean(PrefUtils.IS_REFRESHING, false)) {
            if (mUri != null && FeedDataContentProvider.URI_MATCHER.match(mUri) == FeedDataContentProvider.URI_ENTRIES_FOR_FEED) {
                getActivity().startService(new Intent(getActivity(), FetcherService.class).setAction(FetcherService.ACTION_REFRESH_FEEDS).putExtra(Constants.FEED_ID,
                        mUri.getPathSegments().get(1)));
            } else {
                getActivity().startService(new Intent(getActivity(), FetcherService.class).setAction(FetcherService.ACTION_REFRESH_FEEDS));
            }
        }
        refreshSwipeProgress();
    }

    public Uri getUri() {
        return mOriginalUri;
    }

    public void setData(Uri uri, boolean showFeedInfo) {
        setData(uri, showFeedInfo, false);
    }

    private void setData(Uri uri, boolean showFeedInfo, boolean isSearchUri) {
        mUri = uri;
        if (!isSearchUri) {
            mOriginalUri = mUri;
        }
        mShowFeedInfo = showFeedInfo;

        mEntriesCursorAdapter = new EntriesCursorAdapter(getActivity(), mUri, Constants.EMPTY_CURSOR, mShowFeedInfo);
        setListAdapter(mEntriesCursorAdapter);

        mListDisplayDate = new Date().getTime();
        if (mUri != null) {
            restartLoaders();
        }
        refreshUI();
    }

    private void restartLoaders() {
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.restartLoader(ENTRIES_LOADER_ID, null, mEntriesLoader);
        loaderManager.restartLoader(NEW_ENTRIES_NUMBER_LOADER_ID, null, mEntriesNumberLoader);
    }

    private void refreshUI() {
        if (mNewEntriesNumber > 0) {
            if (mRefreshListBtn.getVisibility() == View.GONE)
                YoYo.with(Techniques.BounceInDown).duration(500).playOn(mRefreshListBtn);
            mRefreshListBtn.setText(getResources().getQuantityString(R.plurals.number_of_new_entries, mNewEntriesNumber, mNewEntriesNumber));
            mRefreshListBtn.setVisibility(View.VISIBLE);
        } else {
            mRefreshListBtn.setVisibility(View.GONE);
        }
    }

    private void refreshSwipeProgress() {
        if (PrefUtils.getBoolean(PrefUtils.IS_REFRESHING, false)) {
            showSwipeProgress();
            mySwipeRefreshLayout.setRefreshing(true);
        } else {
            hideSwipeProgress();
            mySwipeRefreshLayout.setRefreshing(false);
        }
    }


    private class SwipeGestureListener extends SimpleOnGestureListener implements OnTouchListener {
        static final int SWIPE_MIN_DISTANCE = 120;
        static final int SWIPE_MAX_OFF_PATH = 150;
        static final int SWIPE_THRESHOLD_VELOCITY = 150;

        private final GestureDetector mGestureDetector;

        SwipeGestureListener(Context context) {
            mGestureDetector = new GestureDetector(context, this);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (mListView != null && e1 != null && e2 != null && Math.abs(e1.getY() - e2.getY()) <= SWIPE_MAX_OFF_PATH && Math.abs(velocityX) >= SWIPE_THRESHOLD_VELOCITY) {
                long id = mListView.pointToRowId(Math.round(e2.getX()), Math.round(e2.getY()));
                int position = mListView.pointToPosition(Math.round(e2.getX()), Math.round(e2.getY()));
                View view = mListView.getChildAt(position - mListView.getFirstVisiblePosition());

                if (view != null) {
                    // Just click on views, the adapter will do the real stuff
                    if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE) {
                        mEntriesCursorAdapter.toggleReadState(id, view);
                    } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE) {
                        mEntriesCursorAdapter.toggleFavoriteState(id, view);
                    }

                    // Just simulate a CANCEL event to remove the item highlighting
                    // In a post to avoid a crash on 4.0.x
                    mListView.post(() -> {
                        MotionEvent motionEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_CANCEL, 0, 0, 0);
                        mListView.dispatchTouchEvent(motionEvent);
                        motionEvent.recycle();
                    });
                    return true;
                }
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return mGestureDetector.onTouchEvent(event);
        }
    }


}
