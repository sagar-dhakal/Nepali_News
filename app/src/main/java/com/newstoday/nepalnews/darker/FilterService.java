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

package com.newstoday.nepalnews.darker;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.core.app.NotificationCompat;

import com.newstoday.nepalnews.R;
import com.newstoday.nepalnews.activities.MainActivity;

import java.util.Objects;

public class FilterService extends Service {

    private LinearLayout mView;

    public static int STATE;
    private static final int NOTI_STATE;

    private static final int INACTIVE = 0;
    public static final int ACTIVE = 0;
    static int red, green, blue, brightness;

    static {
        STATE = INACTIVE;
        NOTI_STATE = INACTIVE;
    }

    private static int getColor() {
        String hexColour = String.format("%02x%02x%02x%02x", brightness, red, green, blue);
        return (int) Long.parseLong(hexColour, 16);
    }

    public FilterService() {
    }

    @Override
    public void onDestroy() {
        STATE = INACTIVE;
        super.onDestroy();
        if (mView != null) {
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            Objects.requireNonNull(wm).removeView(mView);
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Objects.requireNonNull(notificationManager).cancelAll(); //clear notification when service stops
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences mPrefs;
        if (red == 0 && green == 0 && blue == 0 && brightness == 0) {
            mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            red = mPrefs.getInt("red", 0);
            green = mPrefs.getInt("green", 0);
            blue = mPrefs.getInt("blue", 0);
            brightness = mPrefs.getInt("alpha", 0);
        }
        STATE = ACTIVE;
        mView = new LinearLayout(this);
        mView.setBackgroundColor(getColor());
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.FILL_PARENT,
                WindowManager.LayoutParams.FILL_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                280,
                PixelFormat.TRANSLUCENT
        );
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Objects.requireNonNull(wm).addView(mView, params);

        createNotification();
    }

    @Override
    public IBinder onBind(Intent i) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        STATE = ACTIVE;
        mView.setBackgroundColor(getColor());
        createNotification();
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Objects.requireNonNull(alarmService).set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePI);
    }

    private void createNotification() {
        if (NOTI_STATE == INACTIVE) {
            int id = 100;
            NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "PRIMARY_CHANNEL_ID");
            notification.setContentTitle("Screen Filter Started");
            notification.setContentText("Click here to Stop");
            notification.setSmallIcon(R.drawable.darker_icon);
            notification.setAutoCancel(true)
                    .setOngoing(true);
            notification.setPriority(NotificationCompat.PRIORITY_MAX);

            Intent resultIntent = new Intent(this, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(ScreenFilter_Activity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            notification.setContentIntent(resultPendingIntent);

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Objects.requireNonNull(mNotificationManager).notify(id, notification.build());
        }
    }
}
