package org.wdd.app.android.interestcollection.service.music.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.wdd.app.android.interestcollection.service.music.PlayService;
import org.wdd.app.android.interestcollection.utils.Constants;

/**
 * 来电/耳机拔出时暂停播放
 * Created by wcy on 2016/1/23.
 */
public class NoisyAudioStreamReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PlayService.startCommand(context, Constants.ACTION_MEDIA_PLAY_PAUSE);
    }
}
