package org.wdd.app.android.interestcollection.service.music;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.KSYMediaPlayer;

import org.wdd.app.android.interestcollection.service.music.model.Music;
import org.wdd.app.android.interestcollection.service.music.receiver.NoisyAudioStreamReceiver;
import org.wdd.app.android.interestcollection.utils.Constants;
import org.wdd.app.android.interestcollection.utils.SystemUtils;

import java.io.IOException;

/**
 * 音乐播放后台服务
 * Created by wcy on 2015/11/27.
 */
public class PlayService extends Service implements AudioManager.OnAudioFocusChangeListener {

    private static final int NOTIFICATION_ID = 0x111;
    private static final long TIME_UPDATE = 100L;

    private KSYMediaPlayer ksyMediaPlayer;
    private IntentFilter mNoisyFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private NoisyAudioStreamReceiver mNoisyReceiver = new NoisyAudioStreamReceiver();
    private AudioManager mAudioManager;
    private NotificationManager mNotificationManager;
    private OnPlayerEventListener mListener;
    private Music mPlayingMusic;
    private Handler mHandler = new Handler();

    private boolean isPausing;
    private boolean isPreparing;

    private IMediaPlayer.OnPreparedListener mOnPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {

            // Set Video Scaling Mode
            ksyMediaPlayer.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            //start player
            start();
            if (mListener == null) return;
            mListener.onPlayerPrepared(mp.getDuration());
        }
    };

    private IMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            long duration = ksyMediaPlayer.getDuration();
            long progress = duration * percent / 100;
            if (mListener == null) return;
            mListener.onPlayerCached(progress);
        }
    };

    private IMediaPlayer.OnSeekCompleteListener mOnSeekCompletedListener = new IMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer mp) {
            if (mListener == null) return;
            if(ksyMediaPlayer == null) {
                mListener.onPublish(-1, ksyMediaPlayer.getDuration());
            } else {
                mListener.onPublish(ksyMediaPlayer.getCurrentPosition(), ksyMediaPlayer.getDuration());
            }
        }
    };

    private IMediaPlayer.OnCompletionListener mOnCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer mp) {
        videoPlayEnd();
        }
    };

    private IMediaPlayer.OnErrorListener mOnErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            videoPlayEnd();
            return false;
        }
    };

    public IMediaPlayer.OnInfoListener mOnInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
            switch (i) {
                case KSYMediaPlayer.MEDIA_INFO_BUFFERING_START://Buffering Start
                    break;
                case KSYMediaPlayer.MEDIA_INFO_BUFFERING_END://Buffering End
                    break;
                case KSYMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START://Audio Rendering Start
                    break;
                case KSYMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START://Video Rendering Start
                    break;
                case KSYMediaPlayer.MEDIA_INFO_SUGGEST_RELOAD:
                    // Player find a new stream(video or audio), and we could reload the video.
                    if(ksyMediaPlayer != null)
                        ksyMediaPlayer.reload(mPlayingMusic.getUri(), false, KSYMediaPlayer.KSYReloadMode.KSY_RELOAD_MODE_ACCURATE);
                    break;
                case KSYMediaPlayer.MEDIA_INFO_RELOADED://Succeed to reload video
                    return false;
            }
            return false;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private void videoPlayEnd() {
        ksyMediaPlayer.seekTo(0);
        isPausing = true;
        unregisterReceiver(mNoisyReceiver);
        mHandler.removeCallbacks(mBackgroundRunnable);
        if (mListener == null) return;
        mListener.onPlayeCompletion();
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

    public void play(Music music) {
        try {
            if (ksyMediaPlayer == null) {
                ksyMediaPlayer = new KSYMediaPlayer.Builder(getBaseContext()).build();
                ksyMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
                ksyMediaPlayer.setOnCompletionListener(mOnCompletionListener);
                ksyMediaPlayer.setOnPreparedListener(mOnPreparedListener);
                ksyMediaPlayer.setOnInfoListener(mOnInfoListener);
                ksyMediaPlayer.setOnErrorListener(mOnErrorListener);
                ksyMediaPlayer.setOnSeekCompleteListener(mOnSeekCompletedListener);
//                ksyMediaPlayer.setScreenOnWhilePlaying(true);
                ksyMediaPlayer.setBufferTimeMax(3.0f);
                ksyMediaPlayer.setTimeout(5, 30);
                ksyMediaPlayer.setDecodeMode(KSYMediaPlayer.KSYDecodeMode.KSY_DECODE_MODE_AUTO);//硬解264&265

                try {
                    ksyMediaPlayer.setDataSource(music.getUri());
                    ksyMediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                ksyMediaPlayer.reset();
                ksyMediaPlayer.setDataSource(music.getUri());
                ksyMediaPlayer.prepareAsync();
                isPreparing = true;
                ksyMediaPlayer.setOnPreparedListener(mOnPreparedListener);
                if (mListener != null) {
                    mListener.onChange(music);
                }
            }
            isPausing = false;
            mPlayingMusic = music;
            updateNotification(music);
            mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            registerReceiver(mNoisyReceiver, mNoisyFilter);
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
        ksyMediaPlayer.start();
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

        ksyMediaPlayer.pause();
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
        ksyMediaPlayer.reset();
        ksyMediaPlayer.release();
        ksyMediaPlayer = null;
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
            int msec = Math.round(progress / 100f * ksyMediaPlayer.getDuration());
            ksyMediaPlayer.seekTo(msec);
        }
    }

    public boolean isPlaying() {
        return ksyMediaPlayer != null && ksyMediaPlayer.isPlaying();
    }

    public boolean isPausing() {
        return ksyMediaPlayer != null && isPausing;
    }

    public boolean isPreparing() {
        return ksyMediaPlayer != null && isPreparing;
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

    private Runnable mBackgroundRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlaying() && mListener != null) {
                mListener.onPublish(ksyMediaPlayer.getCurrentPosition(), ksyMediaPlayer.getDuration());
            }

            mHandler.postDelayed(this, TIME_UPDATE);
        }
    };

    public class PlayBinder extends Binder {
        public PlayService getService() {
            return PlayService.this;
        }
    }
}
