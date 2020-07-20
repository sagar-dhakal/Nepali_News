/*
  NepalNews
  <p/>
  Copyright (c) 2019-2020 Sagar Dhakal
  Copyright (C) 2016 Paper Airplane Dev Team
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
package com.newstoday.nepalnews.screenfilter.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.newstoday.nepalnews.screenfilter.Constants;

import java.util.Locale;

public class Settings {

    public static final String PREFERENCES_NAME = "settings";

    public static final String KEY_BRIGHTNESS = "brightness",
            KEY_ADVANCED_MODE = "advanced_mode", KEY_FIRST_RUN = "first_run",
            KEY_DARK_THEME = "dark_theme", KEY_AUTO_MODE = "auto_mode",
            KEY_HOURS_SUNRISE = "hrs_sunrise", KEY_MINUTES_SUNRISE = "min_sunrise",
            KEY_HOURS_SUNSET = "hrs_sunset", KEY_MINUTES_SUNSET = "min_sunset",
            KEY_YELLOW_FILTER_ALPHA = "yellow_filter_alpha", KEY_BUTTON_TIP = "button_tip",
            KEY_HANDLE_VOLUME_KEY = "handle_volume_key", KEY_SHOW_TASK = "show_task",
            KEY_AUTO_ENABLE_WHEN_BRIGHTNESS_CHANGED = "auto_enabled_when_brightness_changed";

    private volatile static Settings sInstance;

    private SharedPreferences mPrefs;

    public static Settings getInstance(Context context) {
        if (sInstance == null) {
            synchronized (Settings.class) {
                if (sInstance == null) {
                    sInstance = new Settings(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    private Settings(Context context) {
        mPrefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_MULTI_PROCESS);
    }

    public void putBoolean(String key, boolean value) {
        mPrefs.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, boolean def) {
        return mPrefs.getBoolean(key, def);
    }

    public Settings putInt(String key, int value) {
        mPrefs.edit().putInt(key, value).apply();
        return this;
    }

    public int getInt(String key, int defValue) {
        return mPrefs.getInt(key, defValue);
    }

    public Settings putString(String key, String value) {
        mPrefs.edit().putString(key, value).apply();
        return this;
    }

    public String getString(String key, String defValue) {
        return mPrefs.getString(key, defValue);
    }

    public int getBrightness(int defValue) {
        return getInt(KEY_BRIGHTNESS, defValue);
    }

    public int getAdvancedMode() {
        return getInt(KEY_ADVANCED_MODE, Constants.AdvancedMode.NONE);
    }

    public boolean isFirstRun() {
        return getBoolean(KEY_FIRST_RUN, true);
    }

    public boolean isDarkTheme() {
        return getBoolean(KEY_DARK_THEME, false);
    }

    public boolean isAutoMode() {
        return getBoolean(KEY_AUTO_MODE, false);
    }

    public void setBrightness(int brightness) {
        putInt(KEY_BRIGHTNESS, brightness);
    }

    public void setAdvancedMode(int advancedMode) {
        putInt(KEY_ADVANCED_MODE, advancedMode);
    }

    public void setFirstRun(boolean isFirstRun) {
        putBoolean(KEY_FIRST_RUN, isFirstRun);
    }

    public void setDarkTheme(boolean useDarkTheme) {
        putBoolean(KEY_DARK_THEME, useDarkTheme);
    }

    public void setHandleVolumeKey(boolean handleVolumeKey) {
        putBoolean(KEY_HANDLE_VOLUME_KEY, handleVolumeKey);
    }

    public boolean shouldHandleVolumeKey() {
        return getBoolean(KEY_HANDLE_VOLUME_KEY, true);
    }

    public void setShowTask(boolean showTask) {
        putBoolean(KEY_SHOW_TASK, showTask);
    }

    public boolean shouldShowTask() {
        return getBoolean(KEY_SHOW_TASK, false);
    }

    public void setAutoEnableWhenBrightnessChanged(boolean newValue) {
        putBoolean(KEY_AUTO_ENABLE_WHEN_BRIGHTNESS_CHANGED, newValue);
    }

    public boolean isAutoEnableWhenBrightnessChanged() {
        return getBoolean(KEY_AUTO_ENABLE_WHEN_BRIGHTNESS_CHANGED, true);
    }

    public String getSunsetTimeText() {
        return String.format(Locale.getDefault(),
                "%1$02d:%2$02d",
                getInt(KEY_HOURS_SUNSET, 0),
                getInt(KEY_MINUTES_SUNSET, 0));
    }

    public String getSunriseTimeText() {
        return String.format(Locale.getDefault(),
                "%1$02d:%2$02d",
                getInt(KEY_HOURS_SUNRISE, 0),
                getInt(KEY_MINUTES_SUNRISE, 0));
    }

    public int getYellowFilterAlpha() {
        return getYellowFilterAlpha(0);
    }

    public int getYellowFilterAlpha(int defValue) {
        return getInt(KEY_YELLOW_FILTER_ALPHA, defValue);
    }

    public void setYellowFilterAlpha(int alpha) {
        putInt(KEY_YELLOW_FILTER_ALPHA, alpha);
    }

    public boolean needButtonTip() {
        return getBoolean(KEY_BUTTON_TIP, true);
    }

    public void setNeedButtonTip(boolean value) {
        putBoolean(KEY_BUTTON_TIP, value);
    }

}
