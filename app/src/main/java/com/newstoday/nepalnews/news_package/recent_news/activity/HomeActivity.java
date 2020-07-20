/*
  NepalNews
  <p/>
  Copyright (c) 2019-2020 Sagar Dhakal
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

package com.newstoday.nepalnews.news_package.recent_news.activity;

import android.app.LoaderManager;
import android.app.job.JobScheduler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.newstoday.nepalnews.Constants;
import com.newstoday.nepalnews.R;
import com.newstoday.nepalnews.activities.MainActivity;
import com.newstoday.nepalnews.news_package.recent_news.fragment.EntriesListFragment;
import com.newstoday.nepalnews.news_package.recent_news.provider.FeedData;
import com.newstoday.nepalnews.news_package.recent_news.provider.FeedData.EntryColumns;
import com.newstoday.nepalnews.news_package.recent_news.provider.FeedData.FeedColumns;
import com.newstoday.nepalnews.news_package.recent_news.provider.FeedDataContentProvider;
import com.newstoday.nepalnews.news_package.recent_news.service.FetcherService;
import com.newstoday.nepalnews.news_package.recent_news.service.RefreshService;
import com.newstoday.nepalnews.news_package.recent_news.utils.PrefUtils;
import com.newstoday.nepalnews.services.Custom_JobSheduler;

import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class HomeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String STATE_CURRENT_DRAWER_POS = "STATE_CURRENT_DRAWER_POS";
    private static final String FEED_UNREAD_NUMBER = "(SELECT " + Constants.DB_COUNT + " FROM " + EntryColumns.TABLE_NAME + " WHERE " +
            EntryColumns.IS_READ + " IS NULL AND " + EntryColumns.FEED_ID + '=' + FeedColumns.TABLE_NAME + '.' + FeedColumns._ID + ')';

    private static final int LOADER_ID = 0;
    private final SharedPreferences.OnSharedPreferenceChangeListener mShowReadListener = (sharedPreferences, key) -> {
        if (PrefUtils.SHOW_READ.equals(key)) {
            getLoaderManager().restartLoader(LOADER_ID, null, HomeActivity.this);

        }
    };
    private EntriesListFragment mEntriesFragment;
    private int mCurrentDrawerPos;
    private int i;
    private final String demo = "News Today";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity_home);
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        MobileAds.initialize(this, initializationStatus -> {
        });
        List<String> testDeviceIds = Collections.singletonList("BEE69BA4713E1A126269D8A901AA9297");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);

        AdRequest adRequest = new AdRequest.Builder().build();
        AdView adView = findViewById(R.id.adView);
        adView.loadAd(adRequest);


        mEntriesFragment = (EntriesListFragment) getFragmentManager().findFragmentById(R.id.entries_list_fragment);
        Toolbar toolbar = findViewById(R.id.base_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        FloatingActionMenu menu = findViewById(R.id.fab);
        menu.getMenuIconView().setImageResource(R.drawable.ic_add);
        FloatingActionButton fab = findViewById(R.id.fab_google);
        FloatingActionButton fab2 = findViewById(R.id.fab_feed);

        String url1 = MainActivity.recentRssLinks.get(i).rssLink1;
        String url2 = MainActivity.recentRssLinks.get(i).rssLink2;
        String url3 = MainActivity.recentRssLinks.get(i).rssLink3;
        String url4 = MainActivity.recentRssLinks.get(i).rssLink4;
        String url5 = MainActivity.recentRssLinks.get(i).rssLink5;
        String url6 = MainActivity.recentRssLinks.get(i).rssLink6;
        String url7 = MainActivity.recentRssLinks.get(i).rssLink7;
        String url8 = MainActivity.recentRssLinks.get(i).rssLink8;
        String url9 = MainActivity.recentRssLinks.get(i).rssLink9;
        String url10 = MainActivity.recentRssLinks.get(i).rssLink10;
        String url11 = MainActivity.recentRssLinks.get(i).rssLink11;
        String url12 = MainActivity.recentRssLinks.get(i).rssLink12;
        String url13 = MainActivity.recentRssLinks.get(i).rssLink13;
        String url14 = MainActivity.recentRssLinks.get(i).rssLink14;
        String url15 = MainActivity.recentRssLinks.get(i).rssLink15;

        if (PrefUtils.getBoolean(PrefUtils.FIRST_OPEN + "a", true)) {
            PrefUtils.putBoolean(PrefUtils.FIRST_OPEN + "a", false);
            add(url1);
            add(url2);
            add(url3);
            add(url4);
            add(url5);
            add(url6);
            add(url7);
            add(url8);
            add(url9);
            add(url10);
            add(url11);
            add(url12);
            add(url13);
            add(url14);
            add(url15);
        } else {
            update(url1, "1");
            update(url2, "2");
            update(url3, "3");
            update(url4, "4");
            update(url5, "5");
            update(url6, "6");
            update(url7, "7");
            update(url8, "8");
            update(url9, "9");
            update(url10, "10");
            update(url11, "11");
            update(url12, "12");
            update(url13, "13");
            update(url14, "14");
            update(url15, "15");
        }


        fab.setColorNormalResId(R.color.colorPrimary);
        fab.setColorPressedResId(R.color.h_dark_red);

        fab2.setColorNormalResId(R.color.colorPrimary);
        fab2.setColorPressedResId(R.color.h_dark_red);

        fab.setImageResource(R.drawable.ic_heart_filled);
        fab2.setImageResource(R.drawable.ic_heart_empty);
        fab.setOnClickListener(v -> selectDrawerItem(1));
        fab2.setOnClickListener(v -> selectDrawerItem(0));

        if (savedInstanceState != null) {
            mCurrentDrawerPos = savedInstanceState.getInt(STATE_CURRENT_DRAWER_POS);
        }
        getLoaderManager().initLoader(LOADER_ID, null, this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PrefUtils.getBoolean(PrefUtils.REFRESH_ENABLED, true)) {
                JobScheduler jobScheduler = (JobScheduler) this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                jobScheduler.cancel(1001);
                Custom_JobSheduler.scheduleNewsJob(this);
            } else {
                JobScheduler jobScheduler = (JobScheduler) this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                jobScheduler.cancel(1001);
            }
        } else {
            if (PrefUtils.getBoolean(PrefUtils.REFRESH_ENABLED, true)) {
                // starts the service independent to this activity
                startService(new Intent(this, RefreshService.class));
            } else {
                stopService(new Intent(this, RefreshService.class));
            }
        }


        if (PrefUtils.getBoolean(PrefUtils.REFRESH_ON_OPEN_ENABLED, false)) {
            if (!PrefUtils.getBoolean(PrefUtils.IS_REFRESHING, false)) {
                startService(new Intent(HomeActivity.this, FetcherService.class).setAction(FetcherService.ACTION_REFRESH_FEEDS));
            }
        }

    }

    private void add(String url) {
        FeedDataContentProvider.addFeed(this, url, demo, false);
    }

    private void update(String url, String position) {
        ContentResolver cr = this.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(FeedData.FeedColumns.URL, url);
        values.put(FeedData.FeedColumns.NAME, demo);
        values.put(FeedData.FeedColumns.RETRIEVE_FULLTEXT, false);
        values.put(FeedData.FeedColumns.PRIORITY, position);
        cr.update(FeedData.FeedColumns.CONTENT_URI(position), values, null, null);
    }

    private void selectDrawerItem(int position) {
        mCurrentDrawerPos = position;
        Uri newUri;
        switch (position) {
            case 1:
                newUri = EntryColumns.FAVORITES_CONTENT_URI;
                break;
            case 0:
            default:
                newUri = EntryColumns.ALL_ENTRIES_CONTENT_URI;
                break;
        }

        if (!newUri.equals(mEntriesFragment.getUri())) {
            boolean showFeedInfo = true;
            mEntriesFragment.setData(newUri, true);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_CURRENT_DRAWER_POS, mCurrentDrawerPos);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PrefUtils.registerOnPrefChangeListener(mShowReadListener);
    }

    @Override
    protected void onPause() {
        PrefUtils.unregisterOnPrefChangeListener(mShowReadListener);
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // We reset the current drawer position
        selectDrawerItem(0);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        CursorLoader cursorLoader = new CursorLoader(this,
                FeedColumns.GROUPED_FEEDS_CONTENT_URI,
                new String[]{FeedColumns._ID, FeedColumns.URL, FeedColumns.NAME,
                        FeedColumns.IS_GROUP, FeedColumns.ICON, FeedColumns.LAST_UPDATE,
                        FeedColumns.ERROR, FEED_UNREAD_NUMBER,
                        FeedColumns.IS_GROUP_EXPANDED},
                FeedColumns.IS_GROUP + Constants.DB_IS_TRUE + Constants.DB_OR +
                        FeedColumns.GROUP_ID + Constants.DB_IS_NULL + Constants.DB_OR +
                        FeedColumns.GROUP_ID + " IN (SELECT " + FeedColumns._ID +
                        " FROM " + FeedColumns.TABLE_NAME +
                        " WHERE " + FeedColumns.IS_GROUP_EXPANDED + Constants.DB_IS_TRUE + ")",
                null,
                null
        );
        cursorLoader.setUpdateThrottle(Constants.UPDATE_THROTTLE_DELAY);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        selectDrawerItem(mCurrentDrawerPos);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    @Override
    public void onBackPressed() {
        PrefUtils.putBoolean(PrefUtils.IS_REFRESHING, false);
        super.onBackPressed();
    }
}
