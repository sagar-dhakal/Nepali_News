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

package com.newstoday.nepalnews.rssfeedreader.adapter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.newstoday.nepalnews.MainApplication;
import com.newstoday.nepalnews.R;
import com.newstoday.nepalnews.rssfeedreader.provider.FeedData;
import com.newstoday.nepalnews.rssfeedreader.provider.FeedData.EntryColumns;
import com.newstoday.nepalnews.rssfeedreader.utils.StringUtils;
import com.newstoday.nepalnews.rssfeedreader.utils.UiUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class DrawerAdapter extends BaseAdapter {

    private static final int POS_ID = 0;
    private static final int POS_URL = 1;
    private static final int POS_NAME = 2;
    private static final int POS_IS_GROUP = 3;
    private static final int POS_ICON = 4;
    private static final int POS_LAST_UPDATE = 5;
    private static final int POS_ERROR = 6;
    private static final int POS_UNREAD = 7;
    private static final int POS_IS_GROUP_EXPANDED = 8;

    private static final int GROUP_TEXT_COLOR = Color.parseColor("#BBBBBB");

    private static final String COLON = MainApplication.getContext().getString(R.string.colon);

    private static final int CACHE_MAX_ENTRIES = 100;
    private final Map<Long, String> mFormattedDateCache = new LinkedHashMap<Long, String>(CACHE_MAX_ENTRIES + 1, .75F, true) {
        @Override
        public boolean removeEldestEntry(Entry<Long, String> eldest) {
            return size() > CACHE_MAX_ENTRIES;
        }
    };

    private int mSelectedItem;
    private final Context mContext;
    private Cursor mFeedsCursor;
    private int mAllUnreadNumber, mFavoritesNumber;

    public DrawerAdapter(Context context, Cursor feedCursor) {
        mContext = context;
        mFeedsCursor = feedCursor;

        updateNumbers();
    }

    public void setSelectedItem(int selectedItem) {
        mSelectedItem = selectedItem;
    }


    public void setCursor(Cursor feedCursor) {
        mFeedsCursor = feedCursor;

        updateNumbers();
        notifyDataSetChanged();
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = Objects.requireNonNull(inflater).inflate(R.layout.feed_item_drawer_list, parent, false);

            ViewHolder holder = new ViewHolder();
            holder.iconView = convertView.findViewById(android.R.id.icon);
            holder.titleTxt = convertView.findViewById(android.R.id.text1);
            holder.stateTxt = convertView.findViewById(android.R.id.text2);
            holder.unreadTxt = convertView.findViewById(R.id.unread_count);
            holder.separator = convertView.findViewById(R.id.separator);
            holder.separatorGroup = convertView.findViewById(R.id.separator_group);
            convertView.setTag(R.id.holder, holder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag(R.id.holder);

        if (holder != null) {
            if (position == mSelectedItem) {
                holder.titleTxt.setTextColor(ContextCompat.getColor(mContext, R.color.light_primary_color));
            } else {
                holder.titleTxt.setTextColor(ContextCompat.getColor(mContext, R.color.light_base_text));
            }

            // default init
            holder.iconView.setImageDrawable(null);
            holder.titleTxt.setText("");
            holder.titleTxt.setAllCaps(false);
            holder.stateTxt.setVisibility(View.GONE);
            holder.unreadTxt.setText("");
            convertView.setPadding(0, 0, 0, 0);
            holder.separator.setVisibility(View.GONE);
            holder.separatorGroup.setVisibility(View.GONE);

            if (position == 0 || position == 1) {
                holder.titleTxt.setText(position == 0 ? R.string.all : R.string.favorites);
                holder.iconView.setImageResource(position == 0 ? R.drawable.ic_statusbar_rss : R.drawable.ic_star);
                if (position == mSelectedItem) {
                    holder.iconView.setColorFilter(ContextCompat.getColor(mContext, R.color.light_primary_color));
                } else {
                    holder.iconView.setColorFilter(ContextCompat.getColor(mContext, R.color.light_base_text));
                }

                int unread = position == 0 ? mAllUnreadNumber : mFavoritesNumber;
                if (unread != 0) {
                    holder.unreadTxt.setText(String.valueOf(unread));
                }
                if (position == 1)
                    holder.separator.setVisibility(View.VISIBLE);
            }
            if (mFeedsCursor != null && mFeedsCursor.moveToPosition(position - 2)) {
                holder.titleTxt.setText((mFeedsCursor.isNull(POS_NAME) ? mFeedsCursor.getString(POS_URL) : mFeedsCursor.getString(POS_NAME)));

                if (mFeedsCursor.getInt(POS_IS_GROUP) == 1) {
                    holder.titleTxt.setAllCaps(true);
                    holder.separatorGroup.setVisibility(View.VISIBLE);
                    holder.iconView.setImageResource(isGroupExpanded(position) ? R.drawable.group_expanded : R.drawable.group_collapsed);
                    holder.iconView.setOnClickListener(v -> {
                        ContentResolver cr = MainApplication.getContext().getContentResolver();
                        ContentValues values = new ContentValues();
                        values.put(FeedData.FeedColumns.IS_GROUP_EXPANDED, isGroupExpanded(position) ? null : 1);
                        cr.update(FeedData.FeedColumns.CONTENT_URI(getItemId(position)), values, null, null);
                    });
                    holder.titleTxt.setTextColor(GROUP_TEXT_COLOR);
                    holder.titleTxt.setAllCaps(false);
                    holder.separatorGroup.setVisibility(View.VISIBLE);
                } else {
                    holder.stateTxt.setVisibility(View.VISIBLE);

                    if (mFeedsCursor.isNull(POS_ERROR)) {
                        long timestamp = mFeedsCursor.getLong(POS_LAST_UPDATE);

                        // Date formatting is expensive, look at the cache
                        String formattedDate = mFormattedDateCache.get(timestamp);
                        if (formattedDate == null) {

                            formattedDate = mContext.getString(R.string.update) + COLON;

                            if (timestamp == 0) {
                                formattedDate += mContext.getString(R.string.never);
                            } else {
                                formattedDate += StringUtils.getDateTimeString(timestamp);
                            }

                            mFormattedDateCache.put(timestamp, formattedDate);
                        }

                        holder.stateTxt.setText(formattedDate);
                    } else {
                        holder.stateTxt.setText(new StringBuilder(mContext.getString(R.string.error)).append(COLON).append(mFeedsCursor.getString(POS_ERROR)));
                    }

                    final long feedId = mFeedsCursor.getLong(POS_ID);
                    Bitmap bitmap = UiUtils.getFaviconBitmap(feedId, mFeedsCursor, POS_ICON);

                    if (bitmap != null) {
                        holder.iconView.setImageBitmap(bitmap);
                    } else {
                        ColorGenerator generator = ColorGenerator.DEFAULT;
                        int color = generator.getColor(feedId);
                        TextDrawable textDrawable = TextDrawable.builder().buildRound(holder.titleTxt.getText().toString().substring(0, 1).toUpperCase(), color);
                        holder.iconView.setImageDrawable(textDrawable);
                    }

                    int unread = mFeedsCursor.getInt(POS_UNREAD);
                    if (unread != 0) {
                        holder.unreadTxt.setText(String.valueOf(unread));
                    }
                }
                if ((mFeedsCursor.isNull(POS_NAME) ? mFeedsCursor.getString(POS_URL) : mFeedsCursor.getString(POS_NAME)).startsWith("ERROR:")) {
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = Objects.requireNonNull(inflater).inflate(R.layout.feed_item_drawer_null, parent, false);
                    return convertView;
                }
            }
        }
        return convertView;
    }

    @Override
    public int getCount() {
        if (mFeedsCursor != null) {
            return mFeedsCursor.getCount() + 2;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (mFeedsCursor != null && mFeedsCursor.moveToPosition(position - 2)) {
            return mFeedsCursor.getLong(POS_ID);
        }

        return -1;
    }

    public byte[] getItemIcon(int position) {
        if (mFeedsCursor != null && mFeedsCursor.moveToPosition(position - 2)) {
            return mFeedsCursor.getBlob(POS_ICON);
        }

        return null;
    }

    public String getItemName(int position) {
        if (mFeedsCursor != null && mFeedsCursor.moveToPosition(position - 2)) {
            return mFeedsCursor.isNull(POS_NAME) ? mFeedsCursor.getString(POS_URL) : mFeedsCursor.getString(POS_NAME);
        }

        return null;
    }

    public boolean isItemAGroup(int position) {
        return mFeedsCursor != null && mFeedsCursor.moveToPosition(position - 2) && mFeedsCursor.getInt(POS_IS_GROUP) == 1;

    }

    private boolean isGroupExpanded(int position) {
        return mFeedsCursor != null && mFeedsCursor.moveToPosition(position - 2) && mFeedsCursor.getInt(POS_IS_GROUP_EXPANDED) == 1;
    }

    private void updateNumbers() {
        mAllUnreadNumber = mFavoritesNumber = 0;

        // Gets the numbers of entries (should be in a thread, but it's way easier like this and it shouldn't be so slow)
        Cursor numbers = mContext.getContentResolver().query(EntryColumns.CONTENT_URI, new String[]{FeedData.ALL_UNREAD_NUMBER, FeedData.FAVORITES_NUMBER}, null, null, null);
        if (numbers != null) {
            if (numbers.moveToFirst()) {
                mAllUnreadNumber = numbers.getInt(0);
                mFavoritesNumber = numbers.getInt(1);
            }
            numbers.close();
        }
    }

    private static class ViewHolder {
        ImageView iconView;
        TextView titleTxt;
        TextView stateTxt;
        TextView unreadTxt;
        View separator;
        View separatorGroup;
    }
}
