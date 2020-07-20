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

package com.newstoday.nepalnews.news_package.news_category.health.service;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Html;
import android.text.TextUtils;
import android.util.Xml;
import android.widget.Toast;

import com.newstoday.nepalnews.Constants;
import com.newstoday.nepalnews.MainApplication;
import com.newstoday.nepalnews.R;
import com.newstoday.nepalnews.news_package.news_category.health.parser.RssAtomParser;
import com.newstoday.nepalnews.news_package.news_category.health.provider.FeedData;
import com.newstoday.nepalnews.news_package.news_category.health.provider.FeedData.EntryColumns;
import com.newstoday.nepalnews.news_package.news_category.health.provider.FeedData.FeedColumns;
import com.newstoday.nepalnews.news_package.news_category.health.provider.FeedData.TaskColumns;
import com.newstoday.nepalnews.news_package.news_category.health.utils.NetworkUtils;
import com.newstoday.nepalnews.news_package.news_category.health.utils.PrefUtils;
import com.newstoday.nepalnews.news_package.recent_news.utils.ArticleTextExtractor;
import com.newstoday.nepalnews.news_package.recent_news.utils.HtmlUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FetcherService extends IntentService {
    public static final String ACTION_REFRESH_FEEDS = "com.newstoday.nepalnews.news_package.news_location.province_two.service.REFRESH";
    public static final String ACTION_MOBILIZE_FEEDS = "com.newstoday.nepalnews.news_package.news_location.province_two.service.MOBILIZE_FEEDS";
    private static final String ACTION_DOWNLOAD_IMAGES = "com.newstoday.nepalnews.news_package.news_location.province_two.service.DOWNLOAD_IMAGES";

    private static final int THREAD_NUMBER = 3;
    private static final int MAX_TASK_ATTEMPT = 3;

    private static final int FETCHMODE_DIRECT = 1;
    private static final int FETCHMODE_REENCODE = 2;

    private static final String CHARSET = "charset=";
    private static final String CONTENT_TYPE_TEXT_HTML = "text/html";
    private static final String HREF = "href=\"";

    private static final String HTML_BODY = "<body";
    private static final String ENCODING = "encoding=\"";

    /* Allow different positions of the "rel" attribute w.r.t. the "href" attribute */
    private static final Pattern FEED_LINK_PATTERN = Pattern.compile(
            "[.]*<link[^>]* ((rel=alternate|rel=\"alternate\")[^>]* href=\"[^\"]*\"|href=\"[^\"]*\"[^>]* (rel=alternate|rel=\"alternate\"))[^>]*>",
            Pattern.CASE_INSENSITIVE);

    private final Handler mHandler;

    public FetcherService() {
        super(FetcherService.class.getSimpleName());
        HttpURLConnection.setFollowRedirects(true);
        mHandler = new Handler();
    }

    public static boolean hasMobilizationTask(long entryId) {
        Cursor cursor = MainApplication.getContext().getContentResolver().query(TaskColumns.CONTENT_URI, TaskColumns.PROJECTION_ID,
                TaskColumns.ENTRY_ID + '=' + entryId + Constants.DB_AND + TaskColumns.IMG_URL_TO_DL + Constants.DB_IS_NULL, null, null);

        boolean result = false;
        if (cursor != null) {
            result = cursor.getCount() > 0;
            cursor.close();
        }
        return result;
    }

    public static void addImagesToDownload(String entryId, ArrayList<String> images) {
        if (images != null && !images.isEmpty()) {
            ContentValues[] values = new ContentValues[images.size()];
            for (int i = 0; i < images.size(); i++) {
                values[i] = new ContentValues();
                values[i].put(TaskColumns.ENTRY_ID, entryId);
                values[i].put(TaskColumns.IMG_URL_TO_DL, images.get(i));
            }

            MainApplication.getContext().getContentResolver().bulkInsert(TaskColumns.CONTENT_URI, values);
        }
    }

    public static void addEntriesToMobilize(long[] entriesId) {
        ContentValues[] values = new ContentValues[entriesId.length];
        for (int i = 0; i < entriesId.length; i++) {
            values[i] = new ContentValues();
            values[i].put(TaskColumns.ENTRY_ID, entriesId[i]);
        }

        MainApplication.getContext().getContentResolver().bulkInsert(TaskColumns.CONTENT_URI, values);
    }

    @Override
    public void onHandleIntent(Intent intent) {
        if (intent == null) { // No intent, we quit
            return;
        }

        boolean isFromAutoRefresh = intent.getBooleanExtra(Constants.FROM_AUTO_REFRESH, false);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = Objects.requireNonNull(connectivityManager).getActiveNetworkInfo();
        // Connectivity issue, we quit
        if (networkInfo == null || networkInfo.getState() != NetworkInfo.State.CONNECTED) {
            if (ACTION_REFRESH_FEEDS.equals(intent.getAction()) && !isFromAutoRefresh) {
                // Display a toast in that case
                mHandler.post(() -> Toast.makeText(FetcherService.this, R.string.network_error, Toast.LENGTH_SHORT).show());
            }
            return;
        }

        if (ACTION_MOBILIZE_FEEDS.equals(intent.getAction())) {
            mobilizeAllEntries();
            downloadAllImages();
        } else if (ACTION_DOWNLOAD_IMAGES.equals(intent.getAction())) {
            downloadAllImages();
        } else { // == Constants.ACTION_REFRESH_FEEDS
            PrefUtils.putBoolean(PrefUtils.IS_REFRESHING, true);

            if (isFromAutoRefresh) {
                PrefUtils.putLong(PrefUtils.LAST_SCHEDULED_REFRESH, SystemClock.elapsedRealtime());
            }

            long keepTime = Long.parseLong(PrefUtils.getString(PrefUtils.KEEP_TIME, "15")) * 86400000L;
            long keepDateBorderTime = keepTime > 0 ? System.currentTimeMillis() - keepTime : 0;

            deleteOldEntries(keepDateBorderTime);

            String feedId = intent.getStringExtra(Constants.FEED_ID);
            int newCount = (feedId == null ? refreshFeeds(keepDateBorderTime) : refreshFeed(feedId, keepDateBorderTime));

            if (newCount > 0) {
                if (isFromAutoRefresh) {
                    Cursor cursor = getContentResolver().query(EntryColumns.CONTENT_URI, new String[]{Constants.DB_COUNT}, EntryColumns.WHERE_UNREAD, null, null);
                    Objects.requireNonNull(cursor).moveToFirst();
                    cursor.close();
                }
            }
            mobilizeAllEntries();
            downloadAllImages();
            PrefUtils.putBoolean(PrefUtils.IS_REFRESHING, false);
        }
    }

    private void mobilizeAllEntries() {
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(TaskColumns.CONTENT_URI, new String[]{TaskColumns._ID, TaskColumns.ENTRY_ID, TaskColumns.NUMBER_ATTEMPT},
                TaskColumns.IMG_URL_TO_DL + Constants.DB_IS_NULL, null, null);

        ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        while (Objects.requireNonNull(cursor).moveToNext()) {
            long taskId = cursor.getLong(0);
            long entryId = cursor.getLong(1);
            int nbAttempt = 0;
            if (!cursor.isNull(2)) {
                nbAttempt = cursor.getInt(2);
            }

            boolean success = false;

            Uri entryUri = EntryColumns.CONTENT_URI(entryId);
            Cursor entryCursor = cr.query(entryUri, null, null, null, null);

            if (Objects.requireNonNull(entryCursor).moveToFirst()) {
                if (entryCursor.isNull(entryCursor.getColumnIndex(EntryColumns.MOBILIZED_HTML))) { // If we didn't already mobilized it
                    int linkPos = entryCursor.getColumnIndex(EntryColumns.LINK);
                    int abstractHtmlPos = entryCursor.getColumnIndex(EntryColumns.ABSTRACT);
                    int feedIdPos = entryCursor.getColumnIndex(EntryColumns.FEED_ID);
                    HttpURLConnection connection = null;

                    try {
                        String link = entryCursor.getString(linkPos);
                        String feedId = entryCursor.getString(feedIdPos);
                        Cursor cursorFeed = cr.query(FeedColumns.CONTENT_URI(feedId), null, null, null, null);
                        Objects.requireNonNull(cursorFeed).moveToNext();
                        int cookieNamePosition = cursorFeed.getColumnIndex(FeedColumns.COOKIE_NAME);
                        int cookieValuePosition = cursorFeed.getColumnIndex(FeedColumns.COOKIE_VALUE);
                        String cookieName = cursorFeed.getString(cookieNamePosition);
                        String cookieValue = cursorFeed.getString(cookieValuePosition);
                        int httpAuthLoginPosition = cursorFeed.getColumnIndex(FeedColumns.HTTP_AUTH_LOGIN);
                        int httpAuthPasswordPosition = cursorFeed.getColumnIndex(FeedColumns.HTTP_AUTH_PASSWORD);
                        final String httpAuthLoginValue = cursorFeed.getString(httpAuthLoginPosition);
                        final String httpAuthPassValue = cursorFeed.getString(httpAuthPasswordPosition);
                        cursorFeed.close();

                        // Try to find a text indicator for better content extraction
                        String contentIndicator = null;
                        String text = entryCursor.getString(abstractHtmlPos);
                        if (!TextUtils.isEmpty(text)) {
                            text = Html.fromHtml(text).toString();
                            if (text.length() > 60) {
                                contentIndicator = text.substring(20, 40);
                            }
                        }

                        connection = NetworkUtils.setupConnection(link, cookieName, cookieValue, httpAuthLoginValue, httpAuthPassValue);

                        String mobilizedHtml = ArticleTextExtractor.extractContent(connection.getInputStream(), contentIndicator);

                        if (mobilizedHtml != null) {
                            mobilizedHtml = HtmlUtils.improveHtmlContent(mobilizedHtml, NetworkUtils.getBaseUrl(link));
                            ContentValues values = new ContentValues();
                            values.put(EntryColumns.MOBILIZED_HTML, mobilizedHtml);

                            ArrayList<String> imgUrlsToDownload = null;
                            if (NetworkUtils.needDownloadPictures()) {
                                imgUrlsToDownload = HtmlUtils.getImageURLs(mobilizedHtml);
                            }

                            String mainImgUrl;
                            if (imgUrlsToDownload != null) {
                                mainImgUrl = HtmlUtils.getMainImageURL(imgUrlsToDownload);
                            } else {
                                mainImgUrl = HtmlUtils.getMainImageURL(mobilizedHtml);
                            }

                            if (mainImgUrl != null) {
                                values.put(EntryColumns.IMAGE_URL, mainImgUrl);
                            }

                            if (cr.update(entryUri, values, null, null) > 0) {
                                success = true;
                                operations.add(ContentProviderOperation.newDelete(TaskColumns.CONTENT_URI(taskId)).build());
                                if (imgUrlsToDownload != null && !imgUrlsToDownload.isEmpty()) {
                                    addImagesToDownload(String.valueOf(entryId), imgUrlsToDownload);
                                }
                            }
                        }
                    } catch (Throwable ignored) {
                    } finally {
                        if (connection != null) {
                            connection.disconnect();
                        }
                    }
                } else { // We already mobilized it
                    success = true;
                    operations.add(ContentProviderOperation.newDelete(TaskColumns.CONTENT_URI(taskId)).build());
                }
            }
            entryCursor.close();

            if (!success) {
                if (nbAttempt + 1 > MAX_TASK_ATTEMPT) {
                    operations.add(ContentProviderOperation.newDelete(TaskColumns.CONTENT_URI(taskId)).build());
                } else {
                    ContentValues values = new ContentValues();
                    values.put(TaskColumns.NUMBER_ATTEMPT, nbAttempt + 1);
                    operations.add(ContentProviderOperation.newUpdate(TaskColumns.CONTENT_URI(taskId)).withValues(values).build());
                }
            }
        }

        cursor.close();

        if (!operations.isEmpty()) {
            try {
                cr.applyBatch(FeedData.AUTHORITY, operations);
            } catch (Throwable ignored) {
            }
        }
    }

    private void downloadAllImages() {
        ContentResolver cr = MainApplication.getContext().getContentResolver();
        Cursor cursor = cr.query(TaskColumns.CONTENT_URI, new String[]{TaskColumns._ID, TaskColumns.ENTRY_ID, TaskColumns.IMG_URL_TO_DL,
                TaskColumns.NUMBER_ATTEMPT}, TaskColumns.IMG_URL_TO_DL + Constants.DB_IS_NOT_NULL, null, null);

        ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        while (Objects.requireNonNull(cursor).moveToNext()) {
            long taskId = cursor.getLong(0);
            long entryId = cursor.getLong(1);
            String imgPath = cursor.getString(2);
            int nbAttempt = 0;
            if (!cursor.isNull(3)) {
                nbAttempt = cursor.getInt(3);
            }

            try {
                NetworkUtils.downloadImage(entryId, imgPath);

                // If we are here, everything is OK
                operations.add(ContentProviderOperation.newDelete(TaskColumns.CONTENT_URI(taskId)).build());
            } catch (Exception e) {
                if (nbAttempt + 1 > MAX_TASK_ATTEMPT) {
                    operations.add(ContentProviderOperation.newDelete(TaskColumns.CONTENT_URI(taskId)).build());
                } else {
                    ContentValues values = new ContentValues();
                    values.put(TaskColumns.NUMBER_ATTEMPT, nbAttempt + 1);
                    operations.add(ContentProviderOperation.newUpdate(TaskColumns.CONTENT_URI(taskId)).withValues(values).build());
                }
            }
        }

        cursor.close();

        if (!operations.isEmpty()) {
            try {
                cr.applyBatch(FeedData.AUTHORITY, operations);
            } catch (Throwable ignored) {
            }
        }
    }

    private void deleteOldEntries(long keepDateBorderTime) {
        Cursor cursor = MainApplication.getContext().getContentResolver().query(FeedColumns.CONTENT_URI, new String[]{FeedColumns._ID, FeedColumns.KEEP_TIME}, null, null, null);
        while (Objects.requireNonNull(cursor).moveToNext()) {
            long feedid = cursor.getLong(0);
            long keepTimeLocal = cursor.getLong(1) * 86400000L;
            long keepDateBorderTimeLocal = keepTimeLocal > 0 ? System.currentTimeMillis() - keepTimeLocal : keepDateBorderTime;
            if (keepDateBorderTimeLocal > 0) {
                String where = EntryColumns.DATE + '<' + keepDateBorderTimeLocal + Constants.DB_AND + EntryColumns.WHERE_NOT_FAVORITE + Constants.DB_AND + EntryColumns.FEED_ID + "=" + feedid;
                Cursor cursor2 = MainApplication.getContext().getContentResolver().query(EntryColumns.CONTENT_URI, EntryColumns.PROJECTION_ID, where, null, null);
                while (Objects.requireNonNull(cursor2).moveToNext()) {
                    int entryId = cursor2.getInt(0);
                    NetworkUtils.deleteEntryImagesCache(entryId, keepDateBorderTimeLocal);
                }
                cursor2.close();
                MainApplication.getContext().getContentResolver().delete(EntryColumns.CONTENT_URI, where, null);
            }
        }
        cursor.close();
    }

    private int refreshFeeds(final long keepDateBorderTime) {
        ContentResolver cr = getContentResolver();
        final Cursor cursor = cr.query(FeedColumns.CONTENT_URI, FeedColumns.PROJECTION_ID, null, null, null);
        int nbFeed = Objects.requireNonNull(cursor).getCount();

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUMBER, r -> {
            Thread t = new Thread(r);
            t.setPriority(Thread.MIN_PRIORITY);
            return t;
        });

        CompletionService<Integer> completionService = new ExecutorCompletionService<>(executor);
        while (cursor.moveToNext()) {
            final String feedId = cursor.getString(0);
            completionService.submit(() -> {
                int result = 0;
                try {
                    result = refreshFeed(feedId, keepDateBorderTime);
                } catch (Exception ignored) {
                }
                return result;
            });
        }
        cursor.close();

        int globalResult = 0;
        for (int i = 0; i < nbFeed; i++) {
            try {
                Future<Integer> f = completionService.take();
                globalResult += f.get();
            } catch (Exception ignored) {
            }
        }

        executor.shutdownNow(); // To purge all threads

        return globalResult;
    }

    private int refreshFeed(String feedId, long keepDateBorderTime) {
        RssAtomParser handler = null;

        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(FeedColumns.CONTENT_URI(feedId), null, null, null, null);

        if (Objects.requireNonNull(cursor).moveToFirst()) {
            int urlPosition = cursor.getColumnIndex(FeedColumns.URL);
            int idPosition = cursor.getColumnIndex(FeedColumns._ID);
            int titlePosition = cursor.getColumnIndex(FeedColumns.NAME);
            int fetchModePosition = cursor.getColumnIndex(FeedColumns.FETCH_MODE);
            int keepTimePosition = cursor.getColumnIndex(FeedColumns.KEEP_TIME);
            int realLastUpdatePosition = cursor.getColumnIndex(FeedColumns.REAL_LAST_UPDATE);
            int iconPosition = cursor.getColumnIndex(FeedColumns.ICON);
            int retrieveFullscreenPosition = cursor.getColumnIndex(FeedColumns.RETRIEVE_FULLTEXT);
            int httpAuthLoginPosition = cursor.getColumnIndex(FeedColumns.HTTP_AUTH_LOGIN);
            int httpAuthPasswordPosition = cursor.getColumnIndex(FeedColumns.HTTP_AUTH_PASSWORD);
            final String httpAuthLoginValue = cursor.getString(httpAuthLoginPosition);
            final String httpAuthPassValue = cursor.getString(httpAuthPasswordPosition);

            String id = cursor.getString(idPosition);

            HttpURLConnection connection = null;

            try {
                String feedUrl = cursor.getString(urlPosition);
                connection = NetworkUtils.setupConnection(feedUrl, httpAuthLoginValue, httpAuthPassValue);
                String contentType = connection.getContentType();
                int fetchMode = cursor.getInt(fetchModePosition);

                long keepTimeLocal = cursor.getInt(keepTimePosition) * 86400000L;
                long keepDateBorderTimeLocal = keepTimeLocal > 0 ? System.currentTimeMillis() - keepTimeLocal : keepDateBorderTime;

                handler = new RssAtomParser(new Date(cursor.getLong(realLastUpdatePosition)), keepDateBorderTimeLocal, id, cursor.getString(titlePosition), feedUrl,
                        cursor.getInt(retrieveFullscreenPosition) == 1);
                handler.setFetchImages(NetworkUtils.needDownloadPictures());

                if (fetchMode == 0) {
                    if (contentType != null && contentType.startsWith(CONTENT_TYPE_TEXT_HTML)) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                        String line;
                        int posStart = -1;

                        while ((line = reader.readLine()) != null) {
                            if (line.contains(HTML_BODY)) {
                                break;
                            } else {
                                Matcher matcher = FEED_LINK_PATTERN.matcher(line);

                                if (matcher.find()) { // not "while" as only one link is needed
                                    line = matcher.group();
                                    posStart = line.indexOf(HREF);

                                    if (posStart > -1) {
                                        String url = line.substring(posStart + 6, line.indexOf('"', posStart + 10)).replace(Constants.AMP_SG,
                                                Constants.AMP);

                                        ContentValues values = new ContentValues();

                                        if (url.startsWith(Constants.SLASH)) {
                                            int index = feedUrl.indexOf('/', 8);

                                            if (index > -1) {
                                                url = feedUrl.substring(0, index) + url;
                                            } else {
                                                url = feedUrl + url;
                                            }
                                        } else if (!url.startsWith(Constants.HTTP_SCHEME) && !url.startsWith(Constants.HTTPS_SCHEME)) {
                                            url = feedUrl + '/' + url;
                                        }
                                        values.put(FeedColumns.URL, url);
                                        cr.update(FeedColumns.CONTENT_URI(id), values, null, null);
                                        connection.disconnect();
                                        connection = NetworkUtils.setupConnection(new URL(url));
                                        contentType = connection.getContentType();
                                        break;
                                    }
                                }
                            }
                        }
                        // this indicates a badly configured feed
                        if (posStart == -1) {
                            connection.disconnect();
                            connection = NetworkUtils.setupConnection(new URL(feedUrl));
                            contentType = connection.getContentType();
                        }
                    }

                    if (contentType != null) {
                        int index = contentType.indexOf(CHARSET);
                        if (index > -1) {
                            int index2 = contentType.indexOf(';', index);

                            try {
                                Xml.findEncodingByName(index2 > -1 ? contentType.substring(index + 8, index2) : contentType.substring(index + 8));
                                fetchMode = FETCHMODE_DIRECT;
                            } catch (UnsupportedEncodingException ignored) {
                                fetchMode = FETCHMODE_REENCODE;
                            }
                        } else {
                            fetchMode = FETCHMODE_REENCODE;
                        }

                    } else {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                        char[] chars = new char[20];

                        int length = bufferedReader.read(chars);

                        String xmlDescription = new String(chars, 0, length);

                        connection.disconnect();
                        connection = NetworkUtils.setupConnection(connection.getURL());

                        int start = xmlDescription.indexOf(ENCODING);

                        if (start > -1) {
                            try {
                                Xml.findEncodingByName(xmlDescription.substring(start + 10, xmlDescription.indexOf('"', start + 11)));
                                fetchMode = FETCHMODE_DIRECT;
                            } catch (UnsupportedEncodingException ignored) {
                                fetchMode = FETCHMODE_REENCODE;
                            }
                        } else {
                            // absolutely no encoding information found
                            fetchMode = FETCHMODE_DIRECT;
                        }
                    }

                    ContentValues values = new ContentValues();
                    values.put(FeedColumns.FETCH_MODE, fetchMode);
                    cr.update(FeedColumns.CONTENT_URI(id), values, null, null);
                }

                switch (fetchMode) {
                    default:
                    case FETCHMODE_DIRECT: {
                        if (contentType != null) {
                            int index = contentType.indexOf(CHARSET);
                            int index2 = contentType.indexOf(';', index);

                            InputStream inputStream = connection.getInputStream();
                            Xml.parse(inputStream,
                                    Xml.findEncodingByName(index2 > -1 ? contentType.substring(index + 8, index2) : contentType.substring(index + 8)),
                                    handler);
                        } else {
                            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                            Xml.parse(reader, handler);
                        }
                        break;
                    }
                    case FETCHMODE_REENCODE: {
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        InputStream inputStream = connection.getInputStream();

                        byte[] byteBuffer = new byte[4096];

                        int n;
                        while ((n = inputStream.read(byteBuffer)) > 0) {
                            outputStream.write(byteBuffer, 0, n);
                        }

                        String xmlText = outputStream.toString();

                        int start = xmlText.indexOf(ENCODING);

                        if (start > -1) {
                            Xml.parse(
                                    new StringReader(new String(outputStream.toByteArray(),
                                            xmlText.substring(start + 10, xmlText.indexOf('"', start + 11)))), handler
                            );
                        } else {
                            // use content type
                            if (contentType != null) {
                                int index = contentType.indexOf(CHARSET);
                                if (index > -1) {
                                    int index2 = contentType.indexOf(';', index);

                                    try {
                                        StringReader reader = new StringReader(new String(outputStream.toByteArray(), index2 > -1 ? contentType.substring(
                                                index + 8, index2) : contentType.substring(index + 8)));
                                        Xml.parse(reader, handler);
                                    } catch (Exception ignored) {
                                    }
                                } else {
                                    StringReader reader = new StringReader(xmlText);
                                    Xml.parse(reader, handler);
                                }
                            }
                        }
                        break;
                    }
                }

                connection.disconnect();
            } catch (FileNotFoundException e) {
                if (handler == null || (handler.isDone() && handler.isCancelled())) {
                    ContentValues values = new ContentValues();

                    // resets the fetch mode to determine it again later
                    values.put(FeedColumns.FETCH_MODE, 0);

                    values.put(FeedColumns.ERROR, getString(R.string.error_feed_error));
                    cr.update(FeedColumns.CONTENT_URI(id), values, null, null);
                }
            } catch (Throwable e) {
                if (handler == null || (handler.isDone() && handler.isCancelled())) {
                    ContentValues values = new ContentValues();

                    // resets the fetch mode to determine it again later
                    values.put(FeedColumns.FETCH_MODE, 0);

                    values.put(FeedColumns.ERROR, e.getMessage() != null ? e.getMessage() : getString(R.string.error_feed_process));
                    cr.update(FeedColumns.CONTENT_URI(id), values, null, null);
                }
            } finally {

                /* check and optionally find favicon */
                try {
                    if (handler != null && cursor.getBlob(iconPosition) == null) {
                        String feedLink = handler.getFeedLink();
                        if (feedLink != null) {
                            NetworkUtils.retrieveFavicon(this, new URL(feedLink), id);
                        } else {
                            NetworkUtils.retrieveFavicon(this, connection.getURL(), id);
                        }
                    }
                } catch (Throwable ignored) {
                }

                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        cursor.close();

        return handler != null ? handler.getNewCount() : 0;
    }
}
