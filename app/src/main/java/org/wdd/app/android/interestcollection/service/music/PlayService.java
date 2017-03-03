package org.wdd.app.android.interestcollection.service.music;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import org.wdd.app.android.interestcollection.service.music.model.Music;
import org.wdd.app.android.interestcollection.service.music.receiver.NoisyAudioStreamReceiver;
import org.wdd.app.android.interestcollection.utils.Constants;
import org.wdd.app.android.interestcollection.utils.SystemUtils;

import java.io.IOException;

/**
 * 音乐播放后台服务
 * Created by wcy on 2015/11/27.
 */
public class PlayService extends Service implements MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    private static final int NOTIFICATION_ID = 0x111;
    private static final long TIME_UPDATE = 100L;

    private MediaPlayer mPlayer = new MediaPlayer();
    private IntentFilter mNoisyFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private NoisyAudioStreamReceiver mNoisyReceiver = new NoisyAudioStreamReceiver();
    private Handler mHandler = new Handler();
    private AudioManager mAudioManager;
    private NotificationManager mNotificationManager;
    private OnPlayerEventListener mListener;
    private Music mPlayingMusic;

    private boolean isPausing;
    private boolean isPreparing;

    @Override
    public void onCreate() {
        super.onCreate();
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mPlayer.setOnCompletionListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new PlayBinder();
    }

    public static void startCommand(Context context, String action) {
        Intent intent = new Intent(context, PlayService.class);
        intent.setAction(action);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case Constants.ACTION_MEDIA_PLAY_PAUSE:
                    playPause();
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    public static boolean isRunning(Context context) {
        return SystemUtils.isServiceRunning(context, PlayService.class);
    }

    public void setOnPlayEventListener(OnPlayerEventListener listener) {
        mListener = listener;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mPlayer.seekTo(0);
        isPausing = true;
        mHandler.removeCallbacks(mBackgroundRunnable);
        if (mListener == null) return;
        mListener.onPlayeCompletion();
    }

    public void play(Music music) {
        try {
            mPlayer.reset();
            mPlayer.setDataSource(music.getUri());
            mPlayer.prepareAsync();
            isPreparing = true;
            mPlayer.setOnPreparedListener(mPreparedListener);
            if (mListener != null) {
                mListener.onChange(music);
            }
            mPlayingMusic = music;
            updateNotification(music);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playPause() {
        if (isPreparing()) {
            return;
        }

        if (isPlaying()) {
            pause();
        } else if (isPausing()) {
            resume();
        }
    }

    private void start() {
        mPlayer.start();
        isPausing = false;
        mHandler.post(mBackgroundRunnable);
        updateNotification(mPlayingMusic);
        mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        registerReceiver(mNoisyReceiver, mNoisyFilter);
    }

    private void pause() {
        if (!isPlaying()) {
            return;
        }

        mPlayer.pause();
        isPausing = true;
        mHandler.removeCallbacks(mBackgroundRunnable);
        cancelNotification(mPlayingMusic);
        mAudioManager.abandonAudioFocus(this);
        unregisterReceiver(mNoisyReceiver);
        if (mListener != null) {
            mListener.onPlayerPause();
        }
    }

    private void resume() {
        if (!isPausing()) {
            return;
        }

        start();
        if (mListener != null) {
            mListener.onPlayerResume();
        }
    }

    public void stop() {
        pause();
        mPlayer.reset();
        mPlayer.release();
        mPlayer = null;
        mNotificationManager.cancel(NOTIFICATION_ID);
        stopSelf();
    }

    /**
     * 跳转到指定的时间位置
     *
     * @param progress 进度
     */
    public void seekTo(int progress) {
        if (isPlaying() || isPausing()) {
            int msec = Math.round(progress / 100f * mPlayer.getDuration());
            mPlayer.seekTo(msec);
            if (mListener != null) {
                mListener.onPublish(msec, mPlayer.getDuration());
            }
        }
    }

    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            isPreparing = false;
            start();
            if (mListener == null) return;
            mListener.onPlayerPrepared(mp.getDuration());
        }
    };

    public boolean isPlaying() {
        return mPlayer != null && mPlayer.isPlaying();
    }

    public boolean isPausing() {
        return mPlayer != null && isPausing;
    }

    public boolean isPreparing() {
        return mPlayer != null && isPreparing;
    }

    /**
     * 获取正在播放的歌曲[本地|网络]
     */
    public Music getPlayingMusic() {
        return mPlayingMusic;
    }

    /**
     * 更新通知栏
     */
    private void updateNotification(Music music) {
        startForeground(NOTIFICATION_ID, SystemUtils.createNotification(this, music));
    }

    private void cancelNotification(Music music) {
        stopForeground(true);
        mNotificationManager.notify(NOTIFICATION_ID, SystemUtils.createNotification(this, music));
    }

    private Runnable mBackgroundRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlaying() && mListener != null) {
                mListener.onPublish(mPlayer.getCurrentPosition(), mPlayer.getDuration());
            }

            mHandler.postDelayed(this, TIME_UPDATE);
        }
    };

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (isPlaying()) {
                    pause();
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class PlayBinder extends Binder {
        public PlayService getService() {
            return PlayService.this;
        }
    }
}
