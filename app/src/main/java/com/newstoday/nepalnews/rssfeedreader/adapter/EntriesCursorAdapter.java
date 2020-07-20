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

package com.newstoday.nepalnews.rssfeedreader.adapter;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.newstoday.nepalnews.MainApplication;
import com.newstoday.nepalnews.R;
import com.newstoday.nepalnews.rssfeedreader.Constants;
import com.newstoday.nepalnews.rssfeedreader.provider.FeedData;
import com.newstoday.nepalnews.rssfeedreader.provider.FeedData.EntryColumns;
import com.newstoday.nepalnews.rssfeedreader.provider.FeedData.FeedColumns;
import com.newstoday.nepalnews.rssfeedreader.utils.CircleTransform;
import com.newstoday.nepalnews.rssfeedreader.utils.NetworkUtils;
import com.newstoday.nepalnews.rssfeedreader.utils.PrefUtils;
import com.newstoday.nepalnews.rssfeedreader.utils.StringUtils;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class EntriesCursorAdapter extends ResourceCursorAdapter {

    private final Uri mUri;
    private final boolean mShowFeedInfo;
    private final CircleTransform mCircleTransform = new CircleTransform();
    private int mIdPos;
    private int mTitlePos;
    private int mMainImgPos;
    private int mDatePos;
    private int mAuthorPos;
    private int mIsReadPos;
    private int mFavoritePos;
    private int mFeedIdPos;
    private int mFeedNamePos;

    public EntriesCursorAdapter(Context context, Uri uri, Cursor cursor, boolean showFeedInfo) {
        super(context, R.layout.feed_item_entry_list, cursor, 0);
        mUri = uri;
        mShowFeedInfo = showFeedInfo;
        reinit(cursor);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        View view;
        int type = cursor.getPosition();
        String adPosition = String.valueOf(type);
        if (adPosition.endsWith("5")) {
            view = LayoutInflater.from(context).inflate(R.layout.aa_feed_recycler_native, parent, false);
            holder.frameLayout = view.findViewById(R.id.adFrame);
            if (holder.frameLayout != null) {
                AdLoader adLoader = new AdLoader.Builder(Objects.requireNonNull(context), context.getString(R.string.native_ad))
                        .forUnifiedNativeAd(unifiedNativeAd -> {
                            UnifiedNativeAdView adView = (UnifiedNativeAdView) LayoutInflater.from(context)
                                    .inflate(R.layout.aa_radio_native, null);
                            populateUnifiedNativeAdView(unifiedNativeAd, adView);
                            holder.frameLayout.removeAllViews();
                            holder.frameLayout.addView(adView);
                        })
                        .withAdListener(new AdListener() {
                            @Override
                            public void onAdFailedToLoad(int errorCode) {

                            }
                        })
                        .withNativeAdOptions(new NativeAdOptions.Builder()
                                .build())
                        .build();
                adLoader.loadAd(new AdRequest.Builder().build());


                Handler handler = new Handler();
                int delay = 15000; //milliseconds
                final int[] a = {0};
                try {
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            a[0] = a[0] + 1;
                            if (a[0] >= 5) {
                                handler.removeCallbacksAndMessages(null);
                            } else {
                                AdLoader adLoader = new AdLoader.Builder(Objects.requireNonNull(context), context.getString(R.string.native_ad))
                                        .forUnifiedNativeAd(unifiedNativeAd -> {
                                            UnifiedNativeAdView adView = (UnifiedNativeAdView) LayoutInflater.from(context)
                                                    .inflate(R.layout.aa_radio_native, null);
                                            populateUnifiedNativeAdView(unifiedNativeAd, adView);
                                            holder.frameLayout.removeAllViews();
                                            holder.frameLayout.addView(adView);
                                        })
                                        .withAdListener(new AdListener() {
                                            @Override
                                            public void onAdFailedToLoad(int errorCode) {

                                            }
                                        })
                                        .withNativeAdOptions(new NativeAdOptions.Builder()
                                                .build())
                                        .build();
                                adLoader.loadAd(new AdRequest.Builder().build());
                                handler.postDelayed(this, delay);
                            }
                        }
                    }, delay);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.feed_item_entry_list, parent, false);
        }
        holder.titleTextView = view.findViewById(android.R.id.text1);
        holder.dateTextView = view.findViewById(android.R.id.text2);
        holder.authorTextView = view.findViewById(R.id.author);
        holder.mainImgView = view.findViewById(R.id.main_icon);
        holder.starImgView = view.findViewById(R.id.favorite_icon);
        holder.readImgView = view.findViewById(R.id.read_icon);
        view.setTag(R.id.holder, holder);
        return view;
    }

    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {

        final ViewHolder holder = (ViewHolder) view.getTag(R.id.holder);
        if (holder.titleTextView != null) {
            if (view.getTag(R.id.holder) == null) {
                ViewHolder holder1 = new ViewHolder();
                holder1.titleTextView = view.findViewById(android.R.id.text1);
                holder1.dateTextView = view.findViewById(android.R.id.text2);
                holder1.authorTextView = view.findViewById(R.id.author);
                holder1.mainImgView = view.findViewById(R.id.main_icon);
                holder1.starImgView = view.findViewById(R.id.favorite_icon);
                holder1.readImgView = view.findViewById(R.id.read_icon);
                view.setTag(R.id.holder, holder1);
            }

            String titleText = cursor.getString(mTitlePos);
            holder.titleTextView.setText(titleText);

            String authorText = cursor.getString(mAuthorPos);
            if (authorText == null || authorText.isEmpty()) {
                holder.authorTextView.setVisibility(View.GONE);
            } else {
                holder.authorTextView.setText(authorText);
                holder.authorTextView.setVisibility(View.VISIBLE);
            }

            final long entryID = cursor.getLong(mIdPos);
            final long feedId = cursor.getLong(mFeedIdPos);
            String feedName = cursor.getString(mFeedNamePos);

            String mainImgUrl = cursor.getString(mMainImgPos);
            mainImgUrl = TextUtils.isEmpty(mainImgUrl) ? null : NetworkUtils.getDownloadedOrDistantImageUrl(entryID, mainImgUrl);

            ColorGenerator generator = ColorGenerator.DEFAULT;
            int color = generator.getColor(feedId); // The color is specific to the feedId (which shouldn't change)
            TextDrawable letterDrawable = TextDrawable.builder().buildRound((feedName != null ? feedName.substring(0, 1).toUpperCase() : ""), color);
            if (mainImgUrl != null && PrefUtils.getBoolean(PrefUtils.DISPLAY_IMAGES, true)) {
                Picasso.get().load(mainImgUrl).transform(mCircleTransform).placeholder(letterDrawable).error(letterDrawable).into(holder.mainImgView);
            } else {
                Picasso.get().cancelRequest(holder.mainImgView);
                holder.mainImgView.setImageDrawable(letterDrawable);
            }

            holder.isFavorite = cursor.getInt(mFavoritePos) == 1;
            UpdateStarImgView(holder);
            holder.starImgView.setOnClickListener(v -> toggleFavoriteState(entryID, view));
            holder.readImgView.setOnClickListener(v -> toggleReadState(entryID, view));


            long now = System.currentTimeMillis();
            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(cursor.getLong(mDatePos), now, DateUtils.SECOND_IN_MILLIS);
            String pub = ago.toString();

            if (mShowFeedInfo && mFeedNamePos > -1 || pub.startsWith("in") || pub.equals("Jan 1, 1970") | pub.equals("1 Jan 1970")) {
                if (feedName != null) {
                    holder.dateTextView.setText(Html.fromHtml("<font color='#247ab0'>" + feedName + "</font>" + Constants.COMMA_SPACE + pub));
                } else {
                    holder.dateTextView.setText(pub);
                }
            } else {
                holder.dateTextView.setText(StringUtils.getDateTimeString(cursor.getLong(mDatePos)));
            }

            if (cursor.isNull(mIsReadPos)) {
                holder.titleTextView.setEnabled(true);
                holder.dateTextView.setEnabled(true);
                holder.authorTextView.setEnabled(true);
                holder.isRead = false;
            } else {
                holder.titleTextView.setEnabled(false);
                holder.dateTextView.setEnabled(false);
                holder.authorTextView.setEnabled(false);
                holder.isRead = true;
            }
        }
    }

    private void UpdateStarImgView(ViewHolder holder) {
        int starred = R.drawable.favorite_light;
        holder.starImgView.setImageResource(holder.isFavorite ? starred : R.drawable.unstar_light);
    }

    public void toggleReadState(final long id, View view) {
        final ViewHolder holder = (ViewHolder) view.getTag(R.id.holder);

        if (holder != null) { // should not happen, but I had a crash with this on PlayStore...
            holder.isRead = !holder.isRead;

            if (holder.isRead) {
                holder.titleTextView.setEnabled(false);
                holder.dateTextView.setEnabled(false);
            } else {
                holder.titleTextView.setEnabled(true);
                holder.dateTextView.setEnabled(true);
            }

            new Thread() {
                @Override
                public void run() {
                    ContentResolver cr = MainApplication.getContext().getContentResolver();
                    Uri entryUri = ContentUris.withAppendedId(mUri, id);
                    cr.update(entryUri, holder.isRead ? FeedData.getReadContentValues() : FeedData.getUnreadContentValues(), null, null);
                }
            }.start();
        }
    }

    public void toggleFavoriteState(final long id, View view) {
        final ViewHolder holder = (ViewHolder) view.getTag(R.id.holder);
        if (holder != null) { // should not happen, but I had a crash with this on PlayStore...
            holder.isFavorite = !holder.isFavorite;

            new Thread() {
                @Override
                public void run() {
                    ContentValues values = new ContentValues();
                    values.put(EntryColumns.IS_FAVORITE, holder.isFavorite ? 1 : 0);

                    ContentResolver cr = MainApplication.getContext().getContentResolver();
                    Uri entryUri = ContentUris.withAppendedId(mUri, id);
                    cr.update(entryUri, values, null, null);
                }
            }.start();
        }
    }


    @Override
    public void changeCursor(Cursor cursor) {
        reinit(cursor);
        super.changeCursor(cursor);
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        reinit(newCursor);
        return super.swapCursor(newCursor);
    }

    @Override
    public void notifyDataSetChanged() {
        reinit(null);
        super.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetInvalidated() {
        reinit(null);
        super.notifyDataSetInvalidated();
    }

    private void reinit(Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            mIdPos = cursor.getColumnIndex(EntryColumns._ID);
            mTitlePos = cursor.getColumnIndex(EntryColumns.TITLE);
            mMainImgPos = cursor.getColumnIndex(EntryColumns.IMAGE_URL);
            mDatePos = cursor.getColumnIndex(EntryColumns.DATE);
            mAuthorPos = cursor.getColumnIndex(EntryColumns.AUTHOR);
            mIsReadPos = cursor.getColumnIndex(EntryColumns.IS_READ);
            mFavoritePos = cursor.getColumnIndex(EntryColumns.IS_FAVORITE);
            mFeedNamePos = cursor.getColumnIndex(FeedColumns.NAME);
            mFeedIdPos = cursor.getColumnIndex(EntryColumns.FEED_ID);
            int mFeedIconPos = cursor.getColumnIndex(FeedColumns.ICON);
        }
    }

    private static class ViewHolder {
        FrameLayout frameLayout;
        TextView authorTextView;
        TextView dateTextView;
        TextView titleTextView;
        ImageView mainImgView;
        ImageView starImgView;
        ImageView readImgView;
        boolean isRead;
        boolean isFavorite;
    }

    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        adView.setMediaView(adView.findViewById(R.id.native_ad_media_view));
        adView.setHeadlineView(adView.findViewById(R.id.native_ad_headline));
        adView.setCallToActionView(adView.findViewById(R.id.native_ad_call_to_action_button));
        adView.setBodyView(adView.findViewById(R.id.native_ad_body));

        if (nativeAd.getMediaContent() == null) {
            adView.getMediaView().setVisibility(View.INVISIBLE);
        } else {
            adView.getMediaView().setVisibility(View.VISIBLE);
            adView.getMediaView().setImageScaleType(ImageView.ScaleType.CENTER_CROP);
            (adView.getMediaView()).setMediaContent(nativeAd.getMediaContent());
        }

        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }


        if (nativeAd.getHeadline() == null) {
            adView.getHeadlineView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
            adView.getHeadlineView().setVisibility(View.VISIBLE);
        }
        adView.setNativeAd(nativeAd);

    }

}
