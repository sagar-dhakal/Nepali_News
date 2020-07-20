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

package com.newstoday.nepalnews.radio.radioplayer_service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;

import org.greenrobot.eventbus.EventBus;

public class RadioManager {
    private static RadioManager instance;
    private static RadioService service;
    private final Context context;
    private boolean isServiceConnected;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            RadioManager.service = ((RadioService.LocalBinder) iBinder).getService();
            RadioManager.this.isServiceConnected = true;
        }

        public void onServiceDisconnected(ComponentName componentName) {
            RadioManager.this.isServiceConnected = false;
        }
    };

    private RadioManager(Context context2) {
        this.context = context2;
        this.isServiceConnected = false;
    }

    public static RadioManager with(Context context2) {
        if (instance == null) {
            instance = new RadioManager(context2);
        }
        return instance;
    }

    public static RadioService getService() {
        return service;
    }

    public void play(String str, Bitmap bitmap, String radioName, String radioDetail, String radioImage) {
        service.playOrPause(str, bitmap, radioName, radioDetail, radioImage);
    }

    public void pause() {
        service.pause();
    }

    public boolean isPlaying() {
        return service.isPlaying();
    }

    public void bind() {
        Intent intent = new Intent(this.context, RadioService.class);
        this.context.startService(intent);
        this.context.bindService(intent, this.serviceConnection, Context.BIND_AUTO_CREATE);
        if (service != null) {
            EventBus.getDefault().post(service.getStatus());
        }
    }

    public void unbind() {
        instance = null;
        this.context.unbindService(this.serviceConnection);
    }
}
