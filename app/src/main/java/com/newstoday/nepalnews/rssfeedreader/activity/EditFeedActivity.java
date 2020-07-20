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
  <p/>
  <p/>
  Some parts of this software are based on "Sparse rss" under the MIT license (see
  below). Please refers to the original project to identify which parts are under the
  MIT license.
  <p/>
  Copyright (c) 2010-2012 Stefan Handschuh
  <p/>
  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:
  <p/>
  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.
  <p/>
  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  THE SOFTWARE.
 */

package com.newstoday.nepalnews.rssfeedreader.activity;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;

import com.newstoday.nepalnews.R;
import com.newstoday.nepalnews.rssfeedreader.Constants;
import com.newstoday.nepalnews.rssfeedreader.adapter.FiltersCursorAdapter;
import com.newstoday.nepalnews.rssfeedreader.loader.BaseLoader;
import com.newstoday.nepalnews.rssfeedreader.provider.FeedData.FeedColumns;
import com.newstoday.nepalnews.rssfeedreader.provider.FeedData.FilterColumns;
import com.newstoday.nepalnews.rssfeedreader.provider.FeedDataContentProvider;
import com.newstoday.nepalnews.rssfeedreader.utils.NetworkUtils;
import com.newstoday.nepalnews.rssfeedreader.utils.UiUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class EditFeedActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    static final String FEED_SEARCH_TITLE = "title";
    static final String FEED_SEARCH_URL = "feedId";
    static final String FEED_SEARCH_DESC = "description";
    private static final String STATE_CURRENT_TAB = "STATE_CURRENT_TAB";
    private static final String[] FEED_PROJECTION = new String[]{FeedColumns.NAME, FeedColumns.URL, FeedColumns.RETRIEVE_FULLTEXT, FeedColumns.COOKIE_NAME, FeedColumns.COOKIE_VALUE, FeedColumns.HTTP_AUTH_LOGIN, FeedColumns.HTTP_AUTH_PASSWORD, FeedColumns.KEEP_TIME};
    private final ActionMode.Callback mFilterActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a home_menu resource providing context home_menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.edit_context_menu, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual home_menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {
                case R.id.menu_edit:
                    Cursor c = mFiltersCursorAdapter.getCursor();
                    if (c.moveToPosition(mFiltersCursorAdapter.getSelectedFilter())) {
                        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_filter_edit, null);
                        final EditText filterText = dialogView.findViewById(R.id.filterText);
                        final CheckBox regexCheckBox = dialogView.findViewById(R.id.regexCheckBox);
                        final RadioButton applyTitleRadio = dialogView.findViewById(R.id.applyTitleRadio);
                        final RadioButton applyContentRadio = dialogView.findViewById(R.id.applyContentRadio);
                        final RadioButton acceptRadio = dialogView.findViewById(R.id.acceptRadio);
                        final RadioButton rejectRadio = dialogView.findViewById(R.id.rejectRadio);

                        filterText.setText(c.getString(c.getColumnIndex(FilterColumns.FILTER_TEXT)));
                        regexCheckBox.setChecked(c.getInt(c.getColumnIndex(FilterColumns.IS_REGEX)) == 1);
                        if (c.getInt(c.getColumnIndex(FilterColumns.IS_APPLIED_TO_TITLE)) == 1) {
                            applyTitleRadio.setChecked(true);
                        } else {
                            applyContentRadio.setChecked(true);
                        }
                        if (c.getInt(c.getColumnIndex(FilterColumns.IS_ACCEPT_RULE)) == 1) {
                            acceptRadio.setChecked(true);
                        } else {
                            rejectRadio.setChecked(true);
                        }

                        final long filterId = mFiltersCursorAdapter.getItemId(mFiltersCursorAdapter.getSelectedFilter());
                        new AlertDialog.Builder(EditFeedActivity.this) //
                                .setTitle(R.string.filter_edit_title) //
                                .setView(dialogView) //
                                .setPositiveButton(android.R.string.ok, (dialog, which) -> new Thread() {
                                    @Override
                                    public void run() {
                                        String filter = filterText.getText().toString();
                                        if (!filter.isEmpty()) {
                                            ContentResolver cr = getContentResolver();
                                            ContentValues values = new ContentValues();
                                            values.put(FilterColumns.FILTER_TEXT, filter);
                                            values.put(FilterColumns.IS_REGEX, regexCheckBox.isChecked());
                                            values.put(FilterColumns.IS_APPLIED_TO_TITLE, applyTitleRadio.isChecked());
                                            values.put(FilterColumns.IS_ACCEPT_RULE, acceptRadio.isChecked());
                                            if (cr.update(FilterColumns.CONTENT_URI, values, FilterColumns._ID + '=' + filterId, null) > 0) {
                                                cr.notifyChange(
                                                        FilterColumns.FILTERS_FOR_FEED_CONTENT_URI(Objects.requireNonNull(getIntent().getData()).getLastPathSegment()),
                                                        null);
                                            }
                                        }
                                    }
                                }.start()).setNegativeButton(android.R.string.cancel, null).show();
                    }

                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.menu_delete:
                    final long filterId = mFiltersCursorAdapter.getItemId(mFiltersCursorAdapter.getSelectedFilter());
                    new AlertDialog.Builder(EditFeedActivity.this) //
                            .setIcon(android.R.drawable.ic_dialog_alert) //
                            .setTitle(R.string.filter_delete_title) //
                            .setMessage(R.string.question_delete_filter) //
                            .setPositiveButton(android.R.string.yes, (dialog, which) -> new Thread() {
                                @Override
                                public void run() {
                                    ContentResolver cr = getContentResolver();
                                    if (cr.delete(FilterColumns.CONTENT_URI, FilterColumns._ID + '=' + filterId, null) > 0) {
                                        cr.notifyChange(FilterColumns.FILTERS_FOR_FEED_CONTENT_URI(Objects.requireNonNull(getIntent().getData()).getLastPathSegment()),
                                                null);
                                    }
                                }
                            }.start()).setNegativeButton(android.R.string.no, null).show();

                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mFiltersCursorAdapter.setSelectedFilter(-1);
            mFiltersListView.invalidateViews();
        }
    };
    private TabHost mTabHost;
    private EditText mNameEditText, mUrlEditText;
    private EditText mCookieNameEditText, mCookieValueEditText;
    private EditText mLoginHTTPAuthEditText, mPasswordHTTPAuthEditText;
    private Spinner mKeepTime;
    private CheckBox mRetrieveFulltextCb;
    private ListView mFiltersListView;
    private FiltersCursorAdapter mFiltersCursorAdapter;
    private Button ok;
    private Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_activity_feed_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        setResult(RESULT_CANCELED);

        Intent intent = getIntent();

        mTabHost = findViewById(R.id.tabHost);
        mNameEditText = findViewById(R.id.feed_title);
        TextView mNameTextView = findViewById(R.id.name_textview);
        mUrlEditText = findViewById(R.id.feed_url);
        mCookieNameEditText = findViewById(R.id.feed_cookiename);
        mCookieValueEditText = findViewById(R.id.feed_cookievalue);
        mLoginHTTPAuthEditText = findViewById(R.id.feed_loginHttpAuth);
        mPasswordHTTPAuthEditText = findViewById(R.id.feed_passwordHttpAuth);
        mKeepTime = findViewById(R.id.settings_keep_times);
        mRetrieveFulltextCb = findViewById(R.id.retrieve_fulltext);
        mFiltersListView = findViewById(android.R.id.list);
        View tabWidget = findViewById(android.R.id.tabs);
        View buttonLayout = findViewById(R.id.button_layout);
        ok = findViewById(R.id.ok);
        cancel = findViewById(R.id.cancel);
        ok.setOnClickListener(v -> onClickOk());
        cancel.setOnClickListener(v -> onClickCancel());
        mTabHost.setup();
        mTabHost.addTab(mTabHost.newTabSpec("feedTab").setIndicator(getString(R.string.tab_feed_title)).setContent(R.id.feed_tab));
        mTabHost.addTab(mTabHost.newTabSpec("filtersTab").setIndicator(getString(R.string.tab_filters_title)).setContent(R.id.filters_tab));
        mTabHost.addTab(mTabHost.newTabSpec("advancedTab").setIndicator(getString(R.string.tab_advanced_title)).setContent(R.id.advanced_tab));

        mTabHost.setOnTabChangedListener(s -> invalidateOptionsMenu());

        if (savedInstanceState != null) {
            mTabHost.setCurrentTab(savedInstanceState.getInt(STATE_CURRENT_TAB));
        }

        switch (Objects.requireNonNull(intent.getAction())) {
            case Intent.ACTION_INSERT:
            case Intent.ACTION_SEND: {
                setTitle(R.string.new_feed_title);

                //Forcing the keyboard to appear
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                tabWidget.setVisibility(View.GONE);
                mNameEditText.setVisibility(View.INVISIBLE);
                mNameTextView.setVisibility(View.INVISIBLE);


                if (intent.hasExtra(Intent.EXTRA_TEXT)) {
                    mUrlEditText.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
                }
                String[] selectedValues = getResources().getStringArray(R.array.settings_keep_time_values);
                mKeepTime.setSelection(selectedValues.length - 1);
                mRetrieveFulltextCb.setChecked(false);
                break;
            }
            case Intent.ACTION_VIEW: {
                setTitle(R.string.new_feed_title);

                tabWidget.setVisibility(View.GONE);
                mUrlEditText.setText(intent.getDataString());
                String[] selectedValues = getResources().getStringArray(R.array.settings_keep_time_values);
                mKeepTime.setSelection(selectedValues.length - 1);
                mRetrieveFulltextCb.setChecked(false);
                break;
            }
            case Intent.ACTION_EDIT:
                setTitle(R.string.edit_feed_title);

                buttonLayout.setVisibility(View.GONE);

                mFiltersCursorAdapter = new FiltersCursorAdapter(this, Constants.EMPTY_CURSOR);
                mFiltersListView.setAdapter(mFiltersCursorAdapter);
                mFiltersListView.setOnItemLongClickListener((parent, view, position, id) -> {
                    startSupportActionMode(mFilterActionModeCallback);
                    mFiltersCursorAdapter.setSelectedFilter(position);
                    mFiltersListView.invalidateViews();
                    return true;
                });

                getLoaderManager().initLoader(0, null, this);

                if (savedInstanceState == null) {
                    Cursor cursor = getContentResolver().query(Objects.requireNonNull(intent.getData()), FEED_PROJECTION, null, null, null);

                    if (Objects.requireNonNull(cursor).moveToNext()) {
                        mNameEditText.setText(cursor.getString(0));
                        mUrlEditText.setText(cursor.getString(1));
                        mRetrieveFulltextCb.setChecked(cursor.getInt(2) == 1);
                        mCookieNameEditText.setText(cursor.getString(3));
                        mCookieValueEditText.setText(cursor.getString(4));
                        mLoginHTTPAuthEditText.setText(cursor.getString(5));
                        mPasswordHTTPAuthEditText.setText(cursor.getString(6));
                        Integer intDate = cursor.getInt(7);
                        String[] selectedValues = getResources().getStringArray(R.array.settings_keep_time_values);
                        int index = Arrays.asList(selectedValues).indexOf(String.valueOf(intDate));
                        mKeepTime.setSelection(index >= 0 ? index : selectedValues.length - 1);
                        cursor.close();
                    } else {
                        cursor.close();
                        Toast.makeText(EditFeedActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_CURRENT_TAB, mTabHost.getCurrentTab());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        if (Objects.requireNonNull(getIntent().getAction()).equals(Intent.ACTION_EDIT)) {
            String url = mUrlEditText.getText().toString();
            ContentResolver cr = getContentResolver();

            try (Cursor cursor = getContentResolver().query(FeedColumns.CONTENT_URI, FeedColumns.PROJECTION_ID,
                    FeedColumns.URL + Constants.DB_ARG, new String[]{url}, null)) {

                if (cursor != null && cursor.moveToFirst() && !Objects.requireNonNull(Objects.requireNonNull(getIntent().getData()).getLastPathSegment()).equals(cursor.getString(0))) {
                    Toast.makeText(EditFeedActivity.this, R.string.error_feed_url_exists, Toast.LENGTH_LONG).show();
                } else {
                    ContentValues values = new ContentValues();

                    if (!url.startsWith(Constants.HTTP_SCHEME) && !url.startsWith(Constants.HTTPS_SCHEME)) {
                        url = Constants.HTTP_SCHEME + url;
                    }
                    values.put(FeedColumns.URL, url);

                    String name = mNameEditText.getText().toString();
                    String cookieName = mCookieNameEditText.getText().toString();
                    String cookieValue = mCookieValueEditText.getText().toString();
                    String loginHTTPAuth = mLoginHTTPAuthEditText.getText().toString();
                    String passwordHTTPAuth = mPasswordHTTPAuthEditText.getText().toString();

                    values.put(FeedColumns.NAME, name.trim().length() > 0 ? name : null);
                    values.put(FeedColumns.RETRIEVE_FULLTEXT, mRetrieveFulltextCb.isChecked() ? 1 : null);
                    values.put(FeedColumns.COOKIE_NAME, cookieName.trim().length() > 0 ? cookieName : "");
                    values.put(FeedColumns.COOKIE_VALUE, cookieValue.trim().length() > 0 ? cookieValue : "");
                    values.put(FeedColumns.HTTP_AUTH_LOGIN, loginHTTPAuth.trim().length() > 0 ? loginHTTPAuth : "");
                    values.put(FeedColumns.HTTP_AUTH_PASSWORD, passwordHTTPAuth.trim().length() > 0 ? passwordHTTPAuth : "");
                    final TypedArray selectedValues = getResources().obtainTypedArray(R.array.settings_keep_time_values);
                    values.put(FeedColumns.KEEP_TIME, selectedValues.getInt(mKeepTime.getSelectedItemPosition(), 0));
                    values.put(FeedColumns.FETCH_MODE, 0);
                    values.putNull(FeedColumns.ERROR);

                    cr.update(Objects.requireNonNull(getIntent().getData()), values, null, null);
                }
            } catch (Exception ignored) {
            }
        }

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_feed, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mTabHost.getCurrentTab() != 1) {
            menu.findItem(R.id.menu_add_filter).setVisible(false);
            menu.findItem(R.id.menu_help_filter).setVisible(false);
        } else {
            menu.findItem(R.id.menu_add_filter).setVisible(true);
            menu.findItem(R.id.menu_help_filter).setVisible(true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_add_filter: {
                final View dialogView = getLayoutInflater().inflate(R.layout.dialog_filter_edit, null);

                new AlertDialog.Builder(this) //
                        .setTitle(R.string.filter_add_title) //
                        .setView(dialogView) //
                        .setPositiveButton(android.R.string.ok, (dialog, id) -> {
                            String filterText = ((EditText) dialogView.findViewById(R.id.filterText)).getText().toString();
                            if (filterText.length() != 0) {
                                String feedId = Objects.requireNonNull(getIntent().getData()).getLastPathSegment();

                                ContentValues values = new ContentValues();
                                values.put(FilterColumns.FILTER_TEXT, filterText);
                                values.put(FilterColumns.IS_REGEX, ((CheckBox) dialogView.findViewById(R.id.regexCheckBox)).isChecked());
                                values.put(FilterColumns.IS_APPLIED_TO_TITLE, ((RadioButton) dialogView.findViewById(R.id.applyTitleRadio)).isChecked());
                                values.put(FilterColumns.IS_ACCEPT_RULE, ((RadioButton) dialogView.findViewById(R.id.acceptRadio)).isChecked());

                                ContentResolver cr = getContentResolver();
                                cr.insert(FilterColumns.FILTERS_FOR_FEED_CONTENT_URI(feedId), values);
                            }
                        }).setNegativeButton(android.R.string.cancel, (dialog, id) -> {
                }).show();
                return true;
            }
            case R.id.menu_help_filter:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://github.com/Etuldan/spaRSS/wiki/How-to-use-the-Feed-Filter"));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onClickOk() {
        // only in insert mode
        final String name = mNameEditText.getText().toString().trim();
        final String urlOrSearch = mUrlEditText.getText().toString().trim();
        if (urlOrSearch.isEmpty()) {
            UiUtils.showMessage(EditFeedActivity.this, R.string.error_feed_error);
        }

        if (!urlOrSearch.contains(".") || !urlOrSearch.contains("/") || urlOrSearch.contains(" ")) {
            final ProgressDialog pd = new ProgressDialog(EditFeedActivity.this);
            pd.setMessage(getString(R.string.loading));
            pd.setCancelable(true);
            pd.setIndeterminate(true);
            pd.show();

            getLoaderManager().restartLoader(1, null, new LoaderManager.LoaderCallbacks<ArrayList<HashMap<String, String>>>() {

                @Override
                public Loader<ArrayList<HashMap<String, String>>> onCreateLoader(int id, Bundle args) {
                    String encodedSearchText = urlOrSearch;
                    try {
                        encodedSearchText = URLEncoder.encode(urlOrSearch, Constants.UTF8);
                    } catch (UnsupportedEncodingException ignored) {
                    }

                    return new GetFeedSearchResultsLoader(EditFeedActivity.this, encodedSearchText);
                }

                @Override
                public void onLoadFinished(Loader<ArrayList<HashMap<String, String>>> loader, final ArrayList<HashMap<String, String>> data) {
                    pd.cancel();

                    if (data == null) {
                        UiUtils.showMessage(EditFeedActivity.this, R.string.error);
                    } else if (data.isEmpty()) {
                        UiUtils.showMessage(EditFeedActivity.this, R.string.no_result);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditFeedActivity.this);
                        builder.setTitle(R.string.feed_search);

                        // create the grid item mapping
                        String[] from = new String[]{FEED_SEARCH_TITLE, FEED_SEARCH_DESC};
                        int[] to = new int[]{android.R.id.text1, android.R.id.text2};

                        // fill in the grid_item layout
                        SimpleAdapter adapter = new SimpleAdapter(EditFeedActivity.this, data, R.layout.feed_item_search_result, from,
                                to);
                        builder.setAdapter(adapter, (dialog, which) -> {
                            FeedDataContentProvider.addFeed(EditFeedActivity.this, Objects.requireNonNull(data.get(which).get(FEED_SEARCH_URL)), name.isEmpty() ? data.get(which).get(FEED_SEARCH_TITLE) : name, mRetrieveFulltextCb.isChecked());

                            setResult(RESULT_OK);
                            finish();
                        });
                        builder.show();
                    }
                }

                @Override
                public void onLoaderReset(Loader<ArrayList<HashMap<String, String>>> loader) {
                }
            });
        } else {
            FeedDataContentProvider.addFeed(EditFeedActivity.this, urlOrSearch, name, mRetrieveFulltextCb.isChecked());

            setResult(RESULT_OK);
            finish();
        }
    }

    private void onClickCancel() {
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(this, FilterColumns.FILTERS_FOR_FEED_CONTENT_URI(Objects.requireNonNull(getIntent().getData()).getLastPathSegment()),
                null, null, null, FilterColumns.IS_ACCEPT_RULE + Constants.DB_DESC);
        cursorLoader.setUpdateThrottle(Constants.UPDATE_THROTTLE_DELAY);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mFiltersCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFiltersCursorAdapter.swapCursor(Constants.EMPTY_CURSOR);
    }
}

/**
 * A custom Loader that loads feed search results from the google WS.
 */
class GetFeedSearchResultsLoader extends BaseLoader<ArrayList<HashMap<String, String>>> {
    private final String mSearchText;

    public GetFeedSearchResultsLoader(Context context, String searchText) {
        super(context);
        mSearchText = searchText;
    }

    /**
     * This is where the bulk of our work is done. This function is called in a background thread and should generate a new set of data to be
     * published by the loader.
     */
    @Override
    public ArrayList<HashMap<String, String>> loadInBackground() {
        try {
            HttpURLConnection conn = NetworkUtils.setupConnection("http://cloud.feedly.com/v3/search/feeds?count=20&locale=" + getContext().getResources().getConfiguration().locale.getLanguage() + "&query=" + mSearchText);
            try {
                String jsonStr = new String(NetworkUtils.getBytes(conn.getInputStream()));

                // Parse results
                final ArrayList<HashMap<String, String>> results = new ArrayList<>();
                JSONArray entries = new JSONObject(jsonStr).getJSONArray("results");
                for (int i = 0; i < entries.length(); i++) {
                    try {
                        JSONObject entry = (JSONObject) entries.get(i);
                        String url = entry.get(EditFeedActivity.FEED_SEARCH_URL).toString().replace("feed/", "");
                        if (!url.isEmpty()) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put(EditFeedActivity.FEED_SEARCH_TITLE, Html.fromHtml(entry.get(EditFeedActivity.FEED_SEARCH_TITLE).toString())
                                    .toString());
                            map.put(EditFeedActivity.FEED_SEARCH_URL, url);
                            map.put(EditFeedActivity.FEED_SEARCH_DESC, Html.fromHtml(entry.get(EditFeedActivity.FEED_SEARCH_DESC).toString()).toString());

                            results.add(map);
                        }
                    } catch (Exception ignored) {
                    }
                }

                return results;
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
            return null;
        }
    }
}
