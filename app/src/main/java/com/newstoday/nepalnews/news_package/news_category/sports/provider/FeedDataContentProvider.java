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

package com.newstoday.nepalnews.news_package.news_category.sports.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.multidex.BuildConfig;

import com.newstoday.nepalnews.Constants;
import com.newstoday.nepalnews.news_package.news_category.sports.provider.FeedData.EntryColumns;
import com.newstoday.nepalnews.news_package.news_category.sports.provider.FeedData.FeedColumns;
import com.newstoday.nepalnews.news_package.news_category.sports.provider.FeedData.FilterColumns;
import com.newstoday.nepalnews.news_package.news_category.sports.provider.FeedData.TaskColumns;

import java.util.Date;
import java.util.Objects;

public class FeedDataContentProvider extends ContentProvider {

    private static final int URI_GROUPED_FEEDS = 1;
    private static final int URI_GROUPS = 2;
    private static final int URI_GROUP = 3;
    private static final int URI_FEEDS_FOR_GROUPS = 4;
    private static final int URI_FEEDS = 5;
    private static final int URI_FEED = 6;
    private static final int URI_FILTERS = 7;
    private static final int URI_FILTERS_FOR_FEED = 8;
    public static final int URI_ENTRIES_FOR_FEED = 9;
    private static final int URI_ENTRY_FOR_FEED = 10;
    private static final int URI_ENTRIES_FOR_GROUP = 11;
    private static final int URI_ENTRY_FOR_GROUP = 12;
    private static final int URI_ENTRIES = 13;
    private static final int URI_ENTRY = 14;
    private static final int URI_ALL_ENTRIES = 15;
    private static final int URI_ALL_ENTRIES_ENTRY = 16;
    private static final int URI_FAVORITES = 17;
    private static final int URI_FAVORITES_ENTRY = 18;
    private static final int URI_TASKS = 19;
    private static final int URI_TASK = 20;
    public static final int URI_SEARCH = 21;
    private static final int URI_SEARCH_ENTRY = 22;

    public static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(FeedData.AUTHORITY, "grouped_feeds", URI_GROUPED_FEEDS);
        URI_MATCHER.addURI(FeedData.AUTHORITY, "groups", URI_GROUPS);
        URI_MATCHER.addURI(FeedData.AUTHORITY, "groups/#", URI_GROUP);
        URI_MATCHER.addURI(FeedData.AUTHORITY, "groups/#/feeds", URI_FEEDS_FOR_GROUPS);
        URI_MATCHER.addURI(FeedData.AUTHORITY, "feeds", URI_FEEDS);
        URI_MATCHER.addURI(FeedData.AUTHORITY, "feeds/#", URI_FEED);
        URI_MATCHER.addURI(FeedData.AUTHORITY, "feeds/#/entries", URI_ENTRIES_FOR_FEED);
        URI_MATCHER.addURI(FeedData.AUTHORITY, "feeds/#/entries/#", URI_ENTRY_FOR_FEED);
        URI_MATCHER.addURI(FeedData.AUTHORITY, "groups/#/entries", URI_ENTRIES_FOR_GROUP);
        URI_MATCHER.addURI(FeedData.AUTHORITY, "groups/#/entries/#", URI_ENTRY_FOR_GROUP);
        URI_MATCHER.addURI(FeedData.AUTHORITY, "filters", URI_FILTERS);
        URI_MATCHER.addURI(FeedData.AUTHORITY, "feeds/#/filters", URI_FILTERS_FOR_FEED);
        URI_MATCHER.addURI(FeedData.AUTHORITY, "entries", URI_ENTRIES);
        URI_MATCHER.addURI(FeedData.AUTHORITY, "entries/#", URI_ENTRY);
        URI_MATCHER.addURI(FeedData.AUTHORITY, "all_entries", URI_ALL_ENTRIES);
        URI_MATCHER.addURI(FeedData.AUTHORITY, "all_entries/#", URI_ALL_ENTRIES_ENTRY);
        URI_MATCHER.addURI(FeedData.AUTHORITY, "favorites", URI_FAVORITES);
        URI_MATCHER.addURI(FeedData.AUTHORITY, "favorites/#", URI_FAVORITES_ENTRY);
        URI_MATCHER.addURI(FeedData.AUTHORITY, "tasks", URI_TASKS);
        URI_MATCHER.addURI(FeedData.AUTHORITY, "tasks/#", URI_TASK);
        URI_MATCHER.addURI(FeedData.AUTHORITY, "entries/search/*", URI_SEARCH);
        URI_MATCHER.addURI(FeedData.AUTHORITY, "entries/search/*/#", URI_SEARCH_ENTRY);
    }

    private final String[] MAX_PRIORITY = new String[]{"MAX(" + FeedColumns.PRIORITY + ")"};

    private DatabaseHelper mDatabaseHelper;

    public static void addFeed(Context context, String url, String name, boolean retrieveFullText) {
        ContentResolver cr = context.getContentResolver();

        if (!url.startsWith(Constants.HTTP_SCHEME) && !url.startsWith(Constants.HTTPS_SCHEME)) {
            url = Constants.HTTP_SCHEME + url;
        }

        Cursor cursor = cr.query(FeedColumns.CONTENT_URI, null, FeedColumns.URL + Constants.DB_ARG,
                new String[]{url}, null);

        if (Objects.requireNonNull(cursor).moveToFirst()) {
            cursor.close();
        } else {
            cursor.close();
            ContentValues values = new ContentValues();

            values.put(FeedColumns.URL, url);
            values.putNull(FeedColumns.ERROR);

            if (name.trim().length() > 0) {
                values.put(FeedColumns.NAME, name);
            }
            values.put(FeedColumns.RETRIEVE_FULLTEXT, retrieveFullText ? 1 : null);
            cr.insert(FeedColumns.CONTENT_URI, values);
        }
    }

    private static String getSearchWhereClause(String uriSearchParam) {
        uriSearchParam = Uri.decode(uriSearchParam).trim();

        if (!uriSearchParam.isEmpty()) {
            uriSearchParam = DatabaseUtils.sqlEscapeString("%" + Uri.decode(uriSearchParam) + "%");
            return EntryColumns.TITLE + " LIKE " + uriSearchParam + Constants.DB_OR + EntryColumns.ABSTRACT + " LIKE " + uriSearchParam + Constants.DB_OR + EntryColumns.MOBILIZED_HTML + " LIKE " + uriSearchParam;
        } else {
            return "1 = 2"; // to have 0 result with an empty search
        }
    }

    @Override
    public String getType(@NonNull Uri uri) {
        int matchCode = URI_MATCHER.match(uri);

        switch (matchCode) {
            case URI_GROUPED_FEEDS:
            case URI_GROUPS:
            case URI_FEEDS_FOR_GROUPS:
            case URI_FEEDS:
                return "vnd.android.cursor.dir/vnd.spaRSS.feed.sports";
            case URI_GROUP:
            case URI_FEED:
                return "vnd.android.cursor.item/vnd.spaRSS.feed.sports";
            case URI_FILTERS:
            case URI_FILTERS_FOR_FEED:
                return "vnd.android.cursor.dir/vnd.spaRSS.filter.sports";
            case URI_FAVORITES:
            case URI_ALL_ENTRIES:
            case URI_ENTRIES:
            case URI_ENTRIES_FOR_FEED:
            case URI_ENTRIES_FOR_GROUP:
            case URI_SEARCH:
                return "vnd.android.cursor.dir/vnd.spaRSS.entry.sports";
            case URI_FAVORITES_ENTRY:
            case URI_ENTRY:
            case URI_ALL_ENTRIES_ENTRY:
            case URI_ENTRY_FOR_FEED:
            case URI_ENTRY_FOR_GROUP:
            case URI_SEARCH_ENTRY:
                return "vnd.android.cursor.item/vnd.spaRSS.entry.sports";
            case URI_TASKS:
                return "vnd.android.cursor.dir/vnd.spaRSS.task.sports";
            case URI_TASK:
                return "vnd.android.cursor.item/vnd.spaRSS.task.sports";
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DatabaseHelper(getContext());

        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // This is a debug code to allow to visualize the task with the ContentProviderHelper app
        if (uri != null && BuildConfig.DEBUG && com.newstoday.nepalnews.news_package.news_location.province_two.provider.FeedData.CONTENT_AUTHORITY.equals(uri.toString())) {
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.setTables(TaskColumns.TABLE_NAME);
            SQLiteDatabase database = mDatabaseHelper.getReadableDatabase();
            return queryBuilder.query(database, projection, selection, selectionArgs, null, null, sortOrder);
        }

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        int matchCode = URI_MATCHER.match(uri);

        if ((matchCode == URI_FEEDS || matchCode == URI_GROUPS || matchCode == URI_FEEDS_FOR_GROUPS) && sortOrder == null) {
            sortOrder = FeedColumns.PRIORITY;
        }

        switch (matchCode) {
            case URI_GROUPED_FEEDS: {
                queryBuilder.setTables(com.newstoday.nepalnews.news_package.news_location.province_two.provider.FeedData.FEEDS_TABLE_WITH_GROUP_PRIORITY);
                sortOrder = "IFNULL(group_priority, " + FeedColumns.PRIORITY + "), IFNULL(" + FeedColumns.GROUP_ID + ", " + FeedColumns._ID + "), " + FeedColumns.IS_GROUP + " DESC, " + FeedColumns.PRIORITY;
                break;
            }
            case URI_GROUPS: {
                queryBuilder.setTables(FeedColumns.TABLE_NAME);
                queryBuilder.appendWhere(new StringBuilder(FeedColumns.IS_GROUP).append(Constants.DB_IS_TRUE).append(Constants.DB_OR)
                        .append(FeedColumns.GROUP_ID).append(Constants.DB_IS_NULL));
                break;
            }
            case URI_FEEDS_FOR_GROUPS: {
                queryBuilder.setTables(FeedColumns.TABLE_NAME);
                queryBuilder.appendWhere(new StringBuilder(FeedColumns.GROUP_ID).append('=').append(Objects.requireNonNull(uri).getPathSegments().get(1)));
                break;
            }
            case URI_GROUP:
            case URI_FEED: {
                queryBuilder.setTables(FeedColumns.TABLE_NAME);
                queryBuilder.appendWhere(new StringBuilder(FeedColumns._ID).append('=').append(Objects.requireNonNull(uri).getPathSegments().get(1)));
                break;
            }
            case URI_FEEDS: {
                queryBuilder.setTables(FeedColumns.TABLE_NAME);
                queryBuilder.appendWhere(new StringBuilder(FeedColumns.IS_GROUP).append(Constants.DB_IS_NULL));
                break;
            }
            case URI_FILTERS: {
                queryBuilder.setTables(FilterColumns.TABLE_NAME);
                break;
            }
            case URI_FILTERS_FOR_FEED: {
                queryBuilder.setTables(FilterColumns.TABLE_NAME);
                queryBuilder.appendWhere(new StringBuilder(FilterColumns.FEED_ID).append('=').append(Objects.requireNonNull(uri).getPathSegments().get(1)));
                break;
            }
            case URI_ENTRY_FOR_FEED:
            case URI_ENTRY_FOR_GROUP:
            case URI_SEARCH_ENTRY: {
                queryBuilder.setTables(com.newstoday.nepalnews.news_package.news_location.province_two.provider.FeedData.ENTRIES_TABLE_WITH_FEED_INFO);
                queryBuilder.appendWhere(new StringBuilder(EntryColumns._ID).append('=').append(Objects.requireNonNull(uri).getPathSegments().get(3)));
                break;
            }
            case URI_ENTRIES_FOR_FEED: {
                queryBuilder.setTables(com.newstoday.nepalnews.news_package.news_location.province_two.provider.FeedData.ENTRIES_TABLE_WITH_FEED_INFO);
                queryBuilder.appendWhere(new StringBuilder(EntryColumns.FEED_ID).append('=').append(Objects.requireNonNull(uri).getPathSegments().get(1)));
                break;
            }
            case URI_ENTRIES_FOR_GROUP: {
                queryBuilder.setTables(com.newstoday.nepalnews.news_package.news_location.province_two.provider.FeedData.ENTRIES_TABLE_WITH_FEED_INFO);
                queryBuilder.appendWhere(new StringBuilder(FeedColumns.GROUP_ID).append('=').append(Objects.requireNonNull(uri).getPathSegments().get(1)));
                break;
            }
            case URI_ALL_ENTRIES:
            case URI_ENTRIES: {
                queryBuilder.setTables(com.newstoday.nepalnews.news_package.news_location.province_two.provider.FeedData.ENTRIES_TABLE_WITH_FEED_INFO);
                break;
            }
            case URI_SEARCH: {
                queryBuilder.setTables(com.newstoday.nepalnews.news_package.news_location.province_two.provider.FeedData.ENTRIES_TABLE_WITH_FEED_INFO);
                queryBuilder.appendWhere(getSearchWhereClause(Objects.requireNonNull(uri).getPathSegments().get(2)));
                break;
            }
            case URI_FAVORITES_ENTRY:
            case URI_ALL_ENTRIES_ENTRY:
            case URI_ENTRY: {
                queryBuilder.setTables(com.newstoday.nepalnews.news_package.news_location.province_two.provider.FeedData.ENTRIES_TABLE_WITH_FEED_INFO);
                queryBuilder.appendWhere(new StringBuilder(EntryColumns._ID).append('=').append(Objects.requireNonNull(uri).getPathSegments().get(1)));
                break;
            }
            case URI_FAVORITES: {
                queryBuilder.setTables(FeedData.ENTRIES_TABLE_WITH_FEED_INFO);
                queryBuilder.appendWhere(new StringBuilder(EntryColumns.IS_FAVORITE).append(Constants.DB_IS_TRUE));
                break;
            }
            case URI_TASKS: {
                queryBuilder.setTables(TaskColumns.TABLE_NAME);
                break;
            }
            case URI_TASK: {
                queryBuilder.setTables(TaskColumns.TABLE_NAME);
                queryBuilder.appendWhere(new StringBuilder(EntryColumns._ID).append('=').append(Objects.requireNonNull(uri).getPathSegments().get(1)));
                break;
            }
            default:
                throw new IllegalArgumentException("Illegal query. Match code=" + matchCode + "; uri=" + uri);
        }

        SQLiteDatabase database = mDatabaseHelper.getReadableDatabase();

        Cursor cursor = queryBuilder.query(database, projection, selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        long newId;

        int matchCode = URI_MATCHER.match(uri);

        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();

        switch (matchCode) {
            case URI_GROUPS:
            case URI_FEEDS: {
                Cursor cursor;
                if (values.containsKey(FeedColumns.GROUP_ID)) {
                    String groupId = values.getAsString(FeedColumns.GROUP_ID);
                    cursor = query(FeedColumns.FEEDS_FOR_GROUPS_CONTENT_URI(groupId), MAX_PRIORITY, null, null, null);
                } else {
                    cursor = query(FeedColumns.GROUPS_CONTENT_URI, MAX_PRIORITY, null, null, null);
                }

                if (Objects.requireNonNull(cursor).moveToFirst()) { // normally this is always the case with MAX()
                    values.put(FeedColumns.PRIORITY, cursor.getInt(0) + 1);
                } else {
                    values.put(FeedColumns.PRIORITY, 1);
                }
                cursor.close();

                newId = database.insert(FeedColumns.TABLE_NAME, null, values);
                break;
            }
            case URI_FILTERS: {
                newId = database.insert(FilterColumns.TABLE_NAME, null, values);
                break;
            }
            case URI_FILTERS_FOR_FEED: {
                values.put(FilterColumns.FEED_ID, uri.getPathSegments().get(1));
                newId = database.insert(FilterColumns.TABLE_NAME, null, values);
                break;
            }
            case URI_ENTRIES_FOR_FEED: {
                values.put(EntryColumns.FEED_ID, uri.getPathSegments().get(1));
                values.put(EntryColumns.FETCH_DATE, new Date().getTime());
                newId = database.insert(EntryColumns.TABLE_NAME, null, values);
                break;
            }
            case URI_TASKS: {
                newId = database.insert(TaskColumns.TABLE_NAME, null, values);
                break;
            }
            default:
                throw new IllegalArgumentException("Illegal insert. Match code=" + matchCode + "; uri=" + uri);
        }

        if (newId > -1) {
            notifyChangeOnAllUris(matchCode, uri);
            return ContentUris.withAppendedId(uri, newId);
        } else { // This can happen when an insert failed with "ON CONFLICT IGNORE", this is not an error
            return uri;
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values == null) {
            throw new IllegalArgumentException("Illegal update. Uri=" + uri + "; values=" + null);
        }

        int matchCode = URI_MATCHER.match(uri);

        String table;

        StringBuilder where = new StringBuilder();

        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();

        switch (matchCode) {
            case URI_FEED: {
                table = FeedColumns.TABLE_NAME;

                long feedId = Long.parseLong(uri.getPathSegments().get(1));
                where.append(FeedColumns._ID).append('=').append(feedId);

                if (values.containsKey(FeedColumns.PRIORITY)) {
                    Cursor priorityCursor = database.query(FeedColumns.TABLE_NAME, new String[]{FeedColumns.PRIORITY, FeedColumns.GROUP_ID},
                            FeedColumns._ID + "=" + feedId, null, null, null, null);
                    if (priorityCursor.moveToNext()) {
                        int oldPriority = priorityCursor.getInt(0);
                        String oldGroupId = priorityCursor.getString(1);
                        int newPriority = values.getAsInteger(FeedColumns.PRIORITY);
                        String newGroupId = values.getAsString(FeedColumns.GROUP_ID);

                        priorityCursor.close();

                        String oldGroupWhere = '(' + (oldGroupId != null ? FeedColumns.GROUP_ID + '=' + oldGroupId : FeedColumns.IS_GROUP
                                + Constants.DB_IS_TRUE + Constants.DB_OR + FeedColumns.GROUP_ID + Constants.DB_IS_NULL) + ')';

                        // If the group has changed, it is not only a +1 or -1 for priority...
                        if (oldGroupId == null && newGroupId != null || oldGroupId != null && newGroupId == null || oldGroupId != null && !oldGroupId.equals(newGroupId)) {

                            String priorityValue = FeedColumns.PRIORITY + "-1";
                            String priorityWhere = FeedColumns.PRIORITY + '>' + oldPriority;
                            database.execSQL("UPDATE " + FeedColumns.TABLE_NAME + " SET " + FeedColumns.PRIORITY + '=' + priorityValue + " WHERE "
                                    + oldGroupWhere + Constants.DB_AND + priorityWhere);

                            priorityValue = FeedColumns.PRIORITY + "+1";
                            priorityWhere = FeedColumns.PRIORITY + '>' + (newPriority - 1);
                            String newGroupWhere = '(' + (newGroupId != null ? FeedColumns.GROUP_ID + '=' + newGroupId : FeedColumns.IS_GROUP
                                    + Constants.DB_IS_TRUE + Constants.DB_OR + FeedColumns.GROUP_ID + Constants.DB_IS_NULL) + ')';
                            database.execSQL("UPDATE " + FeedColumns.TABLE_NAME + " SET " + FeedColumns.PRIORITY + '=' + priorityValue + " WHERE "
                                    + newGroupWhere + Constants.DB_AND + priorityWhere);

                        } else { // We move the item into the same group
                            if (newPriority > oldPriority) {
                                String priorityValue = FeedColumns.PRIORITY + "-1";
                                String priorityWhere = '(' + FeedColumns.PRIORITY + " BETWEEN " + (oldPriority + 1) + " AND " + newPriority + ')';
                                database.execSQL("UPDATE " + FeedColumns.TABLE_NAME + " SET " + FeedColumns.PRIORITY + '=' + priorityValue + " WHERE "
                                        + oldGroupWhere + Constants.DB_AND + priorityWhere);

                            } else if (newPriority < oldPriority) {
                                String priorityValue = FeedColumns.PRIORITY + "+1";
                                String priorityWhere = '(' + FeedColumns.PRIORITY + " BETWEEN " + newPriority + " AND " + (oldPriority - 1) + ')';
                                database.execSQL("UPDATE " + FeedColumns.TABLE_NAME + " SET " + FeedColumns.PRIORITY + '=' + priorityValue + " WHERE "
                                        + oldGroupWhere + Constants.DB_AND + priorityWhere);
                            }
                        }
                    } else {
                        priorityCursor.close();
                    }
                }
                break;
            }
            case URI_GROUPS:
            case URI_FEEDS_FOR_GROUPS:
            case URI_FEEDS: {
                table = FeedColumns.TABLE_NAME;
                break;
            }
            case URI_FILTERS: {
                table = FilterColumns.TABLE_NAME;
                break;
            }
            case URI_FILTERS_FOR_FEED: {
                table = FilterColumns.TABLE_NAME;
                where.append(FilterColumns.FEED_ID).append('=').append(uri.getPathSegments().get(1));
                break;
            }
            case URI_ENTRY_FOR_FEED:
            case URI_ENTRY_FOR_GROUP:
            case URI_SEARCH_ENTRY: {
                table = EntryColumns.TABLE_NAME;
                where.append(EntryColumns._ID).append('=').append(uri.getPathSegments().get(3));
                break;
            }
            case URI_ENTRIES_FOR_FEED: {
                table = EntryColumns.TABLE_NAME;
                where.append(EntryColumns.FEED_ID).append('=').append(uri.getPathSegments().get(1));
                break;
            }
            case URI_ENTRIES_FOR_GROUP: {
                table = EntryColumns.TABLE_NAME;
                where.append(EntryColumns.FEED_ID).append(" IN (SELECT ").append(FeedColumns._ID).append(" FROM ").append(FeedColumns.TABLE_NAME).append(" WHERE ").append(FeedColumns.GROUP_ID).append('=').append(uri.getPathSegments().get(1)).append(')');
                break;
            }
            case URI_ALL_ENTRIES:
            case URI_ENTRIES: {
                table = EntryColumns.TABLE_NAME;
                break;
            }
            case URI_SEARCH: {
                table = EntryColumns.TABLE_NAME;
                where.append(getSearchWhereClause(uri.getPathSegments().get(2)));
                break;
            }
            case URI_FAVORITES_ENTRY:
            case URI_ALL_ENTRIES_ENTRY:
            case URI_ENTRY: {
                table = EntryColumns.TABLE_NAME;
                where.append(EntryColumns._ID).append('=').append(uri.getPathSegments().get(1));
                break;
            }
            case URI_FAVORITES: {
                table = EntryColumns.TABLE_NAME;
                where.append(EntryColumns.IS_FAVORITE).append(Constants.DB_IS_TRUE);
                break;
            }
            case URI_TASKS: {
                table = TaskColumns.TABLE_NAME;
                break;
            }
            case URI_TASK: {
                table = TaskColumns.TABLE_NAME;
                where.append(TaskColumns._ID).append('=').append(uri.getPathSegments().get(1));
                break;
            }
            default:
                throw new IllegalArgumentException("Illegal update. Match code=" + matchCode + "; uri=" + uri);
        }

        if (!TextUtils.isEmpty(selection)) {
            if (where.length() > 0) {
                where.append(Constants.DB_AND).append(selection);
            } else {
                where.append(selection);
            }
        }

        int count = database.update(table, values, where.toString(), selectionArgs);

        if (count > 0) {
            notifyChangeOnAllUris(matchCode, uri);
        }

        return count;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int matchCode = URI_MATCHER.match(uri);

        String table;

        StringBuilder where = new StringBuilder();

        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();

        switch (matchCode) {
            case URI_GROUP: {
                table = FeedColumns.TABLE_NAME;

                String groupId = uri.getPathSegments().get(1);

                where.append(FeedColumns._ID).append('=').append(groupId);

                // Delete the sub feeds & their entries
                Cursor subFeedsCursor = database.query(FeedColumns.TABLE_NAME, FeedColumns.PROJECTION_ID, FeedColumns.GROUP_ID + "=" + groupId, null,
                        null, null, null);
                while (subFeedsCursor.moveToNext()) {
                    String feedId = subFeedsCursor.getString(0);
                    delete(FeedColumns.CONTENT_URI(feedId), null, null);
                }
                subFeedsCursor.close();

                // Update the priorities
                Cursor priorityCursor = database.query(FeedColumns.TABLE_NAME, FeedColumns.PROJECTION_PRIORITY, FeedColumns._ID + "=" + groupId, null,
                        null, null, null);

                if (priorityCursor.moveToNext()) {
                    int priority = priorityCursor.getInt(0);
                    String priorityWhere = FeedColumns.PRIORITY + " > " + priority;
                    String groupWhere = '(' + FeedColumns.IS_GROUP + Constants.DB_IS_TRUE + Constants.DB_OR + FeedColumns.GROUP_ID + Constants.DB_IS_NULL
                            + ')';
                    database.execSQL("UPDATE " + FeedColumns.TABLE_NAME + " SET " + FeedColumns.PRIORITY + " = " + FeedColumns.PRIORITY + "-1 WHERE "
                            + groupWhere + Constants.DB_AND + priorityWhere);
                }
                priorityCursor.close();
                break;
            }
            case URI_FEED: {
                table = FeedColumns.TABLE_NAME;

                final String feedId = uri.getPathSegments().get(1);

                // Remove also the feed entries & filters
                new Thread() {
                    @Override
                    public void run() {
                        Uri entriesUri = EntryColumns.ENTRIES_FOR_FEED_CONTENT_URI(feedId);
                        delete(entriesUri, null, null);
                        delete(FilterColumns.FILTERS_FOR_FEED_CONTENT_URI(feedId), null, null);
                    }
                }.start();

                where.append(FeedColumns._ID).append('=').append(feedId);

                // Update the priorities
                Cursor priorityCursor = database.query(FeedColumns.TABLE_NAME, new String[]{FeedColumns.PRIORITY, FeedColumns.GROUP_ID},
                        FeedColumns._ID + '=' + feedId, null, null, null, null);

                if (priorityCursor.moveToNext()) {
                    int priority = priorityCursor.getInt(0);
                    String groupId = priorityCursor.getString(1);

                    String groupWhere = '(' + (groupId != null ? FeedColumns.GROUP_ID + '=' + groupId : FeedColumns.IS_GROUP + Constants.DB_IS_TRUE
                            + Constants.DB_OR + FeedColumns.GROUP_ID + Constants.DB_IS_NULL) + ')';
                    String priorityWhere = FeedColumns.PRIORITY + " > " + priority;

                    database.execSQL("UPDATE " + FeedColumns.TABLE_NAME + " SET " + FeedColumns.PRIORITY + " = " + FeedColumns.PRIORITY + "-1 WHERE "
                            + groupWhere + Constants.DB_AND + priorityWhere);
                }
                priorityCursor.close();
                break;
            }
            case URI_FEEDS_FOR_GROUPS: {
                table = FeedColumns.TABLE_NAME;
                where.append(FeedColumns.GROUP_ID).append('=').append(uri.getPathSegments().get(1));
                break;
            }
            case URI_FILTERS: {
                table = FilterColumns.TABLE_NAME;
                break;
            }
            case URI_FILTERS_FOR_FEED: {
                table = FilterColumns.TABLE_NAME;
                where.append(FilterColumns.FEED_ID).append('=').append(uri.getPathSegments().get(1));
                break;
            }
            case URI_ENTRY_FOR_FEED:
            case URI_ENTRY_FOR_GROUP:
            case URI_SEARCH_ENTRY: {
                table = EntryColumns.TABLE_NAME;
                final String entryId = uri.getPathSegments().get(3);
                where.append(EntryColumns._ID).append('=').append(entryId);

                // Also remove the associated tasks
                new Thread() {
                    @Override
                    public void run() {
                        delete(TaskColumns.CONTENT_URI, TaskColumns.ENTRY_ID + '=' + entryId, null);
                    }
                }.start();
                break;
            }
            case URI_ENTRIES_FOR_FEED: {
                table = EntryColumns.TABLE_NAME;
                where.append(EntryColumns.FEED_ID).append('=').append(uri.getPathSegments().get(1));
                break;
            }
            case URI_ENTRIES_FOR_GROUP: {
                table = EntryColumns.TABLE_NAME;
                where.append(EntryColumns.FEED_ID).append(" IN (SELECT ").append(FeedColumns._ID).append(" FROM ").append(FeedColumns.TABLE_NAME).append(" WHERE ").append(FeedColumns.GROUP_ID).append('=').append(uri.getPathSegments().get(1)).append(')');
                break;
            }
            case URI_ALL_ENTRIES:
            case URI_ENTRIES: {
                table = EntryColumns.TABLE_NAME;

                // Also remove all tasks
                new Thread() {
                    @Override
                    public void run() {
                        delete(TaskColumns.CONTENT_URI, null, null);
                    }
                }.start();
                break;
            }
            case URI_FAVORITES_ENTRY:
            case URI_ALL_ENTRIES_ENTRY:
            case URI_ENTRY: {
                table = EntryColumns.TABLE_NAME;
                where.append(EntryColumns._ID).append('=').append(uri.getPathSegments().get(1));
                break;
            }
            case URI_FAVORITES: {
                table = EntryColumns.TABLE_NAME;
                where.append(EntryColumns.IS_FAVORITE).append(Constants.DB_IS_TRUE);
                break;
            }
            case URI_TASKS: {
                table = TaskColumns.TABLE_NAME;
                break;
            }
            case URI_TASK: {
                table = TaskColumns.TABLE_NAME;
                where.append(TaskColumns._ID).append('=').append(uri.getPathSegments().get(1));
                break;
            }
            default:
                throw new IllegalArgumentException("Illegal delete. Match code=" + matchCode + "; uri=" + uri);
        }

        if (!TextUtils.isEmpty(selection)) {
            if (where.length() > 0) {
                where.append(Constants.DB_AND);
            }
            where.append(selection);
        }

        int count = database.delete(table, where.toString(), selectionArgs);

        if (count > 0) {
            notifyChangeOnAllUris(matchCode, uri);
        }
        return count;
    }

    private void notifyChangeOnAllUris(int matchCode, Uri uri) {
        ContentResolver cr = Objects.requireNonNull(getContext()).getContentResolver();
        cr.notifyChange(uri, null);

        if (matchCode != URI_FILTERS && matchCode != URI_FILTERS_FOR_FEED && matchCode != URI_TASKS && matchCode != URI_TASK) {
            // Notify everything else (except EntryColumns.CONTENT_URI to not update the
            // entry WebView when clicking on "favorite" button)
            cr.notifyChange(FeedColumns.GROUPED_FEEDS_CONTENT_URI, null);
            cr.notifyChange(EntryColumns.ALL_ENTRIES_CONTENT_URI, null);
            cr.notifyChange(EntryColumns.FAVORITES_CONTENT_URI, null);
            cr.notifyChange(FeedColumns.CONTENT_URI, null);
            cr.notifyChange(FeedColumns.GROUPS_CONTENT_URI, null);
        }
    }
}
