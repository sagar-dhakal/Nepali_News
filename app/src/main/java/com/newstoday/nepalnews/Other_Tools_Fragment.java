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

package com.newstoday.nepalnews;


import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.newstoday.nepalnews.activities.Activity_Weather;
import com.newstoday.nepalnews.darker.ScreenFilter_Activity;
import com.newstoday.nepalnews.flashlight.MainActivity;
import com.newstoday.nepalnews.radio.Radio_Activity;
import com.newstoday.nepalnews.rssfeedreader.activity.HomeActivity;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class Other_Tools_Fragment extends Fragment {

    public Other_Tools_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.other_tools_fragment, container, false);
        ImageView click_flashlight = view.findViewById(R.id.click_flashlight);
        ImageView click_weather = view.findViewById(R.id.click_weather);
        ImageView click_filter = view.findViewById(R.id.click_filter);
        ImageView click_diary = view.findViewById(R.id.click_diary);
        ImageView click_radio = view.findViewById(R.id.click_radio);
        click_flashlight.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        });
        click_weather.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Activity_Weather.class);
            startActivity(intent);
        });
        click_filter.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createNotificationChannel();
                }
                startActivity(new Intent(getActivity(), com.newstoday.nepalnews.screenfilter.ui.MainActivity.class));
            } else {
                startActivity(new Intent(getActivity(), ScreenFilter_Activity.class));
            }
        });
        click_diary.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), HomeActivity.class);
            startActivity(intent);
        });
        click_radio.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Radio_Activity.class);
            startActivity(intent);
        });
        return view;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createNotificationChannel() {
        NotificationManager notificationManager = Objects.requireNonNull(getActivity()).getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            NotificationChannel channel = new NotificationChannel(
                    com.newstoday.nepalnews.screenfilter.Constants.NOTIFICATION_CHANNEL_ID_RS,
                    getString(R.string.notification_channel_running_status),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setShowBadge(false);
            channel.enableLights(false);
            channel.enableVibration(false);
            channel.setSound(null, null);

            notificationManager.createNotificationChannel(channel);
        }
    }

}
