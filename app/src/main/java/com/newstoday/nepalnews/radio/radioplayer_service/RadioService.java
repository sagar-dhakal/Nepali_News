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

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

public class RadioService extends Service implements Player.EventListener, AudioManager.OnAudioFocusChangeListener {
    private String radioName;
    private String radioDetail;
    private String radioImage;
    private Bitmap bitmap;
    public static final String ACTION_PLAY = "ACTION_PLAY";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static String current_Url;
    private final IBinder iBinder = new LocalBinder();
    private SimpleExoPlayer exoPlayer;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;
    private boolean onGoingCall = false;
    private TelephonyManager telephonyManager;
    private WifiManager.WifiLock wifiLock;
    private AudioManager audioManager;
    private MediaNotificationManager notificationManager;
    private String status;
    private String streamUrl;
    private boolean isRunning = false;

    public void sleepTimer(Context context, long minutes) {
        Thread thread = new Thread() {
            public void run() {
                isRunning = true;
                Looper.prepare();
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    RadioManager.getService().pause();
                    Objects.requireNonNull(Looper.myLooper()).quit();
                }, minutes * 60000);
                Looper.loop();
            }
        };

        if (!isRunning) {
            thread.start();
            Toast.makeText(context, "Radio will be Stopped after " + minutes + " Minutes", Toast.LENGTH_LONG).show();
        } else {
            isRunning = false;
            try {
                thread.interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(context, "Timer Stopped", Toast.LENGTH_LONG).show();
        }
    }

    public class LocalBinder extends Binder {
        public RadioService getService() {
            return RadioService.this;
        }
    }

    private final BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            pause();
        }
    };

    private final PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_OFFHOOK
                    || state == TelephonyManager.CALL_STATE_RINGING) {
                if (!isPlaying()) return;
                onGoingCall = true;
                stop();

            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                if (!onGoingCall) return;
                onGoingCall = false;
                resume();
            }
        }
    };

    private final MediaSessionCompat.Callback mediasSessionCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onPause() {
            super.onPause();
            pause();
        }

        @Override
        public void onStop() {
            super.onStop();
            stop();
            notificationManager.cancelNotify();
        }

        @Override
        public void onPlay() {
            super.onPlay();
            resume();
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        onGoingCall = false;
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        notificationManager = new MediaNotificationManager(this);
        wifiLock = ((WifiManager) Objects.requireNonNull(getApplicationContext().getSystemService(Context.WIFI_SERVICE)))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mcScPAmpLock");
        mediaSession = new MediaSessionCompat(this, getClass().getSimpleName());
        transportControls = mediaSession.getController().getTransportControls();
        mediaSession.setActive(true);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, radioName)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "Online Radio")
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, radioDetail)
                .build());
        mediaSession.setCallback(mediasSessionCallback);

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        Objects.requireNonNull(telephonyManager).listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        exoPlayer = new SimpleExoPlayer.Builder(this).build();
        exoPlayer.addListener(this);

        try {
            registerReceiver(becomingNoisyReceiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
        } catch (Exception e) {
            e.printStackTrace();
        }
        status = PlaybackStatus.IDLE;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (TextUtils.isEmpty(action))
            return START_NOT_STICKY;
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            stop();
            return START_NOT_STICKY;
        }

        if (action.equalsIgnoreCase(ACTION_PLAY)) {
            transportControls.play();

        } else if (action.equalsIgnoreCase(ACTION_PAUSE)) {

            transportControls.pause();

        } else if (action.equalsIgnoreCase(ACTION_STOP)) {
            transportControls.pause();
            transportControls.stop();
            RadioService.this.stopSelf();
        }

        return START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {

        if (status.equals(PlaybackStatus.IDLE))
            stopSelf();

        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(final Intent intent) {

    }

    @Override
    public void onDestroy() {
        pause();
        this.exoPlayer.release();
        this.exoPlayer.removeListener(this);
        TelephonyManager telephonyManager2 = this.telephonyManager;
        if (telephonyManager2 != null) {
            telephonyManager2.listen(this.phoneStateListener, 0);
        }
        this.notificationManager.cancelNotify();
        this.mediaSession.release();
        super.onDestroy();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:

                exoPlayer.setVolume(0.8f);

                resume();

                break;

            case AudioManager.AUDIOFOCUS_LOSS:

                stop();

                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:

                if (isPlaying()) pause();

                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:

                if (isPlaying())
                    exoPlayer.setVolume(0.1f);

                break;
        }

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        switch (playbackState) {
            case Player.STATE_BUFFERING:
                status = PlaybackStatus.LOADING;
                break;
            case Player.STATE_ENDED:
                status = PlaybackStatus.STOPPED;
                break;
            case Player.STATE_READY:
                status = playWhenReady ? PlaybackStatus.PLAYING : PlaybackStatus.PAUSED;
                break;
            case Player.STATE_IDLE:
            default:
                status = PlaybackStatus.IDLE;
                break;
        }

        if (!status.equals(PlaybackStatus.IDLE))
            notificationManager.startNotify(status, bitmap, radioName, radioDetail);

        EventBus.getDefault().post(status);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

        EventBus.getDefault().post(PlaybackStatus.ERROR);
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {
    }

    private void play(String streamUrl) {
        this.streamUrl = streamUrl;
        if (wifiLock != null && !wifiLock.isHeld()) {
            wifiLock.acquire();

        }
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "Hamro Nepali Radio"));
        ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(streamUrl));
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);
    }

    private void resume() {
        if (streamUrl != null)
            play(streamUrl);
    }

    public void pause() {
        exoPlayer.setPlayWhenReady(false);
        audioManager.abandonAudioFocus(this);
        wifiLockRelease();
    }

    private void stop() {
        exoPlayer.stop();
        audioManager.abandonAudioFocus(this);
        wifiLockRelease();
    }

    public void playOrPause(String url, Bitmap bitmap, String radioName, String radioDetail, String radioImage) {
        RadioService.this.bitmap = bitmap;
        RadioService.this.radioName = radioName;
        RadioService.this.radioDetail = radioDetail;
        RadioService.this.radioImage = radioImage;

        current_Url = url;
        if (url != null) {
            if (!isPlaying()) {
                play(url);
            } else {
                pause();
            }
        } else {
            Toast.makeText(this, "Server Error", Toast.LENGTH_SHORT).show();
        }
    }

    public String getStatus() {
        return status;
    }

    public MediaSessionCompat getMediaSession() {
        return mediaSession;
    }

    public boolean isPlaying() {
        return this.status.equals(PlaybackStatus.PLAYING);
    }

    private void wifiLockRelease() {
        if (wifiLock != null && wifiLock.isHeld()) {
            wifiLock.release();
        }
    }

    private String getUserAgent() {
        return Util.getUserAgent(this, getClass().getSimpleName());
    }
}
