package org.wdd.app.android.interestcollection.utils;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.service.music.model.Music;
import org.wdd.app.android.interestcollection.ui.audios.activity.AudioDetailActivity;

import java.util.Locale;

import cn.bmob.v3.helper.NotificationCompat;

/**
 * Created by wangdd on 17-3-2.
 */

public class SystemUtils {

    /**
     * 判断Service是否在运行
     */
    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static Notification createNotification(Context context, Music music) {
        String title = music.getTitle();
        String subtitle = FileUtils.getArtistAndAlbum(music.getArtist(), music.getAlbum());
        Intent intent = new Intent(context, AudioDetailActivity.class);
        intent.putExtra(Constants.FROM_NOTIFICATION, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(subtitle)
                .setSmallIcon(R.mipmap.ic_launcher);
//                .setLargeIcon(cover);
        //
        return builder.getNotification();
    }

    public static String formatTime(String pattern, long milli) {
        int m = (int) (milli / DateUtils.MINUTE_IN_MILLIS);
        int s = (int) ((milli / DateUtils.SECOND_IN_MILLIS) % 60);
        String mm = String.format(Locale.getDefault(), "%02d", m);
        String ss = String.format(Locale.getDefault(), "%02d", s);
        return pattern.replace("mm", mm).replace("ss", ss);
    }

}
