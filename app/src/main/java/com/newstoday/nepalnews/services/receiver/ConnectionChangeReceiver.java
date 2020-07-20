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

package com.newstoday.nepalnews.services.receiver;

import android.annotation.SuppressLint;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.SystemClock;

import com.newstoday.nepalnews.Constants;
import com.newstoday.nepalnews.news_package.recent_news.service.FetcherService;
import com.newstoday.nepalnews.news_package.recent_news.service.RefreshService;
import com.newstoday.nepalnews.news_package.recent_news.utils.PrefUtils;
import com.newstoday.nepalnews.services.Custom_JobSheduler;

public class ConnectionChangeReceiver extends BroadcastReceiver {
    private boolean mConnection = false;

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (mConnection && intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
            mConnection = false;
        } else if (!mConnection && !intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
            mConnection = true;

            if (!com.newstoday.nepalnews.rssfeedreader.utils.PrefUtils.getBoolean(com.newstoday.nepalnews.rssfeedreader.utils.PrefUtils.IS_REFRESHING, false) && com.newstoday.nepalnews.rssfeedreader.utils.PrefUtils.getBoolean(com.newstoday.nepalnews.rssfeedreader.utils.PrefUtils.REFRESH_ENABLED, true)) {
                int time = 3600000;
                try {
                    time = Math.max(60000, Integer.parseInt(com.newstoday.nepalnews.rssfeedreader.utils.PrefUtils.getString(com.newstoday.nepalnews.rssfeedreader.utils.PrefUtils.REFRESH_INTERVAL, com.newstoday.nepalnews.rssfeedreader.service.RefreshService.SIXTY_MINUTES)));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                long lastRefresh = com.newstoday.nepalnews.rssfeedreader.utils.PrefUtils.getLong(com.newstoday.nepalnews.rssfeedreader.utils.PrefUtils.LAST_SCHEDULED_REFRESH, 0);
                long elapsedRealTime = SystemClock.elapsedRealtime();

                // If the system rebooted, we need to reset the last value
                if (elapsedRealTime < lastRefresh) {
                    lastRefresh = 0;
                    com.newstoday.nepalnews.rssfeedreader.utils.PrefUtils.putLong(com.newstoday.nepalnews.rssfeedreader.utils.PrefUtils.LAST_SCHEDULED_REFRESH, 0);
                }

                if (lastRefresh == 0 || elapsedRealTime - lastRefresh > time) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                        jobScheduler.cancel(1010);
                        Custom_JobSheduler.scheduleFeedJob(context);
                    } else {
                        context.startService(new Intent(context, com.newstoday.nepalnews.rssfeedreader.service.FetcherService.class).setAction(com.newstoday.nepalnews.rssfeedreader.service.FetcherService.ACTION_REFRESH_FEEDS).putExtra(Constants.FROM_AUTO_REFRESH, true));
                    }
                }
            }


            if (!PrefUtils.getBoolean(PrefUtils.IS_REFRESHING, false) && PrefUtils.getBoolean(PrefUtils.REFRESH_ENABLED, true)) {
                int time = 3600000;
                try {
                    time = Math.max(60000, Integer.parseInt(PrefUtils.getString(PrefUtils.REFRESH_INTERVAL, RefreshService.SIXTY_MINUTES)));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                long lastRefresh = PrefUtils.getLong(PrefUtils.LAST_SCHEDULED_REFRESH, 0);
                long elapsedRealTime = SystemClock.elapsedRealtime();

                // If the system rebooted, we need to reset the last value
                if (elapsedRealTime < lastRefresh) {
                    lastRefresh = 0;
                    PrefUtils.putLong(PrefUtils.LAST_SCHEDULED_REFRESH, 0);
                }

                if (lastRefresh == 0 || elapsedRealTime - lastRefresh > time) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                        jobScheduler.cancel(1001);
                        Custom_JobSheduler.scheduleNewsJob(context);
                    } else {
                        context.startService(new Intent(context, FetcherService.class).setAction(FetcherService.ACTION_REFRESH_FEEDS).putExtra(Constants.FROM_AUTO_REFRESH, true));
                    }
                }

            }
        }
    }
}