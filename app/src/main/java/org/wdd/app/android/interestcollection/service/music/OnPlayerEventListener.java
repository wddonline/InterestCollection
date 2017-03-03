package org.wdd.app.android.interestcollection.service.music;

import org.wdd.app.android.interestcollection.service.music.model.Music;

/**Music
 * 播放进度监听器
 * Created by hzwangchenyan on 2015/12/17.
 */
public interface OnPlayerEventListener {
    /**
     * 更新进度
     */
    void onPublish(long currentMillions, long duration);

    /**
     * 切换歌曲
     */
    void onChange(Music music);

    /**
     * 暂停播放
     */
    void onPlayerPause();

    /**
     * 继续播放
     */
    void onPlayerResume();

    void onPlayerPrepared(int duration);

    void onPlayeCompletion();
}
