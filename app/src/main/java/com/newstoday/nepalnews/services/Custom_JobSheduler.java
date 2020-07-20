/*
  NepalNews
  <p/>
  Copyright (c) 2019-2020 Sagar Dhakal
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
package com.newstoday.nepalnews.services;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.newstoday.nepalnews.news_package.recent_news.service.Lollipop_RefreshService;
import com.newstoday.nepalnews.news_package.recent_news.utils.PrefUtils;

@RequiresApi(api = Build.VERSION_CODES.M)
public class Custom_JobSheduler {
//    Background Service

    public static final String SIXTY_MINUTES = "7200000";

    public static void scheduleNewsJob(Context context) {
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);

        ComponentName serviceComponent = new ComponentName(context, Lollipop_RefreshService.class);
        JobInfo builder = new JobInfo.Builder(1001, serviceComponent)
                .setMinimumLatency(Integer.parseInt(PrefUtils.getString(PrefUtils.REFRESH_INTERVAL, SIXTY_MINUTES)))
                .setPersisted(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();

        jobScheduler.schedule(builder);
    }

    public static void scheduleFeedJob(Context context) {
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);

        ComponentName serviceComponent = new ComponentName(context, com.newstoday.nepalnews.rssfeedreader.service.Lollipop_RefreshService.class);
        JobInfo builder = new JobInfo.Builder(1010, serviceComponent)
                .setMinimumLatency(Integer.parseInt(com.newstoday.nepalnews.rssfeedreader.utils.PrefUtils.getString(com.newstoday.nepalnews.rssfeedreader.utils.PrefUtils.REFRESH_INTERVAL, SIXTY_MINUTES)))
                .setPersisted(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();

        jobScheduler.schedule(builder);
    }
}
