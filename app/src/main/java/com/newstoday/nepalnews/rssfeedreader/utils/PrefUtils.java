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

package com.newstoday.nepalnews.rssfeedreader.utils;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

import com.newstoday.nepalnews.MainApplication;

public class PrefUtils {
    public static final String SLIDE_AD = "SLIDE_AD";
    private static final String TAG = "PrefUtils_READER";
    public static final String DISPLAY_TIP = "DISPLAY_TIP_READER";
    public static final String IS_REFRESHING = "IS_REFRESHING_READER";
    public static final String REFRESH_INTERVAL = "refresh.interval.reader";
    public static final String REFRESH_ENABLED = "refresh.enabled.reader";
    public static final String REFRESH_ON_OPEN_ENABLED = "refreshonopen.enabled.reader";
    public static final String REFRESH_WIFI_ONLY = "refreshwifionly.enabled.reader";
    public static final String DISPLAY_IMAGES = "display_images.reader";
    public static final String PRELOAD_IMAGE_MODE = "preload_image_mode.reader";
    public static final String DISPLAY_OLDEST_FIRST = "display_oldest_first.reader";
    public static final String DISPLAY_ENTRIES_FULLSCREEN = "display_entries_fullscreen.reader";
    public static final String PROXY_ENABLED = "proxy.enabled.reader";
    public static final String PROXY_PORT = "proxy.port.reader";
    public static final String PROXY_HOST = "proxy.host.reader";
    public static final String PROXY_WIFI_ONLY = "proxy.wifionly.reader";
    public static final String PROXY_TYPE = "proxy.type.reader";
    public static final String KEEP_TIME = "keeptime.reader";
    public static final String FONT_SIZE = "fontsize.reader";
    public static final String LAST_SCHEDULED_REFRESH = "lastscheduledrefresh.reader";
    public static final String SHOW_READ = "show_read.reader";

    public static boolean getBoolean(String key, boolean defValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext());
        return settings.getBoolean(key, defValue);
    }

    public static void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext()).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static int getInt(String key, int defValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext());
        return settings.getInt(key, defValue);
    }

    public static void putInt(String key, int value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext()).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static long getLong(String key, long defValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext());
        return settings.getLong(key, defValue);
    }

    public static void putLong(String key, long value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext()).edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static String getString(String key, String defValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext());
        return settings.getString(key, defValue);
    }

    public static void putString(String key, String value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext()).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void remove(String key) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext()).edit();
        editor.remove(key);
        editor.apply();
    }

    public static void registerOnPrefChangeListener(OnSharedPreferenceChangeListener listener) {
        try {
            PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext()).registerOnSharedPreferenceChangeListener(listener);
        } catch (Exception e) { // Seems to be possible to have a NPE here... Why?? Because MainApplication.getContext() might not be set, yet.
            e.printStackTrace();
        }
    }

    public static void unregisterOnPrefChangeListener(OnSharedPreferenceChangeListener listener) {
        try {
            PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext()).unregisterOnSharedPreferenceChangeListener(listener);
        } catch (Exception e) { // Seems to be possible to have a NPE here... Why?? Because MainApplication.getContext() might not be set, yet.
            e.printStackTrace();
        }
    }
}
