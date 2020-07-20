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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.newstoday.nepalnews.screenfilter.Constants;

import java.util.Calendar;


public final class AlarmUtil {

    public static final int REQUEST_ALARM_SUNRISE = 1002, REQUEST_ALARM_SUNSET = 1003;

    private static final String TAG = AlarmUtil.class.getSimpleName();

    public static boolean isInSleepTime(Context context) {
        Settings settings = Settings.getInstance(context);
        if (settings.getBoolean(Settings.KEY_AUTO_MODE, false)) {
            int hrsSunrise = settings.getInt(Settings.KEY_HOURS_SUNRISE, 6);
            int minSunrise = settings.getInt(Settings.KEY_MINUTES_SUNRISE, 0);
            int hrsSunset = settings.getInt(Settings.KEY_HOURS_SUNSET, 22);
            int minSunset = settings.getInt(Settings.KEY_MINUTES_SUNSET, 0);

            Calendar now = Calendar.getInstance();
            Calendar sunriseCalendar = (Calendar) now.clone();
            Calendar sunsetCalendar = (Calendar) now.clone();

            sunriseCalendar.set(Calendar.HOUR_OF_DAY, hrsSunrise);
            sunriseCalendar.set(Calendar.MINUTE, minSunrise);
            sunriseCalendar.set(Calendar.SECOND, 0);
            sunriseCalendar.set(Calendar.MILLISECOND, 0);

            sunsetCalendar.set(Calendar.HOUR_OF_DAY, hrsSunset);
            sunsetCalendar.set(Calendar.MINUTE, minSunset);
            sunsetCalendar.set(Calendar.SECOND, 0);
            sunsetCalendar.set(Calendar.MILLISECOND, 0);
            if (sunsetCalendar.before(now)) {
                sunriseCalendar.add(Calendar.DATE, 1);
            }
            return !sunriseCalendar.before(now);
        }
        return false;
    }

    public static void updateAlarmSettings(Context context) {
        Settings settings = Settings.getInstance(context);
        if (settings.getBoolean(Settings.KEY_AUTO_MODE, false)) {
            int hrsSunrise = settings.getInt(Settings.KEY_HOURS_SUNRISE, 6);
            int minSunrise = settings.getInt(Settings.KEY_MINUTES_SUNRISE, 0);
            int hrsSunset = settings.getInt(Settings.KEY_HOURS_SUNSET, 22);
            int minSunset = settings.getInt(Settings.KEY_MINUTES_SUNSET, 0);

            Calendar now = Calendar.getInstance();
            Calendar sunriseCalendar = (Calendar) now.clone();
            Calendar sunsetCalendar = (Calendar) now.clone();

            sunriseCalendar.set(Calendar.HOUR_OF_DAY, hrsSunrise);
            sunriseCalendar.set(Calendar.MINUTE, minSunrise);
            sunriseCalendar.set(Calendar.SECOND, 0);
            sunriseCalendar.set(Calendar.MILLISECOND, 0);
            if (sunriseCalendar.before(now)) sunriseCalendar.add(Calendar.DATE, 1);

            sunsetCalendar.set(Calendar.HOUR_OF_DAY, hrsSunset);
            sunsetCalendar.set(Calendar.MINUTE, minSunset);
            sunsetCalendar.set(Calendar.SECOND, 0);
            sunsetCalendar.set(Calendar.MILLISECOND, 0);
            if (sunsetCalendar.before(now)) sunsetCalendar.add(Calendar.DATE, 1);

            Log.i(TAG, "Reset alarm");

            cancelAlarm(context, REQUEST_ALARM_SUNRISE, Constants.ACTION_ALARM_STOP);
            cancelAlarm(context, REQUEST_ALARM_SUNSET, Constants.ACTION_ALARM_START);
            setAlarm(context,
                    AlarmManager.RTC_WAKEUP,
                    sunriseCalendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    REQUEST_ALARM_SUNRISE,
                    Constants.ACTION_ALARM_STOP);
            setAlarm(context,
                    AlarmManager.RTC_WAKEUP,
                    sunsetCalendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    REQUEST_ALARM_SUNSET,
                    Constants.ACTION_ALARM_START);
        } else {
            Log.i(TAG, "Cancel alarm");
            cancelAlarm(context, REQUEST_ALARM_SUNRISE, Constants.ACTION_ALARM_STOP);
            cancelAlarm(context, REQUEST_ALARM_SUNSET, Constants.ACTION_ALARM_START);
        }
    }

    private static void setAlarm(Context context, int type, long triggerAtMillis,
                                 long intervalMillis, int requestCode, String action) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            am.setRepeating(type, triggerAtMillis, intervalMillis, PendingIntent.getBroadcast(context,
                    requestCode, new Intent(action), PendingIntent.FLAG_UPDATE_CURRENT));
        }
    }

    private static void cancelAlarm(Context context, int requestCode, String action) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            am.cancel(PendingIntent.getBroadcast(context,
                    requestCode, new Intent(action), PendingIntent.FLAG_UPDATE_CURRENT));
        }
    }

}
