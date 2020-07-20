package com.newstoday.nepalnews.todo_diary;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import com.newstoday.nepalnews.R;

import java.util.Objects;
import java.util.UUID;


public class AlarmReceiver extends BroadcastReceiver {

    public static final String TODOTEXT = "com.newstoday.nepalnews.todo_diary.todonotificationservicetext";
    public static final String TODOUUID = "com.newstoday.nepalnews.todo_diary.todonotificationserviceuuid";

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        String mTodoText = intent.getStringExtra(TODOTEXT);
        UUID mTodoUUID = (UUID) intent.getSerializableExtra(TODOUUID);
        Intent i = new Intent(context, ReminderActivity.class);
        i.putExtra(TODOUUID, mTodoUUID);
        Intent deleteIntent = new Intent(context, DeleteNotificationService.class);
        deleteIntent.putExtra(TODOUUID, mTodoUUID);

        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setContentTitle("Todo Time")
                .setContentText(mTodoText)
                .setSmallIcon(R.drawable.ic_done)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 350, 350, 350})
                .setDefaults(Notification.DEFAULT_SOUND)
                .setDefaults(Notification.DEFAULT_LIGHTS)
                //do not delete item when notification cancelled
//                    .setDeleteIntent(PendingIntent.getService(context, Objects.requireNonNull(mTodoUUID).hashCode(), deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setContentIntent(PendingIntent.getActivity(context, Objects.requireNonNull(mTodoUUID).hashCode(), i, PendingIntent.FLAG_UPDATE_CURRENT));

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel("101", "Todo_Notification", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            assert mNotificationManager != null;
            notificationBuilder.setChannelId("101");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(101, notificationBuilder.build());
    }
}