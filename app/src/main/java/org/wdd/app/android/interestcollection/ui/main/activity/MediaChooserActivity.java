package org.wdd.app.android.interestcollection.ui.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import org.wdd.app.android.interestcollection.ui.audios.activity.AudioDetailActivity;
import org.wdd.app.android.interestcollection.ui.audios.model.Audio;
import org.wdd.app.android.interestcollection.ui.images.activity.ImageDetailActivity;
import org.wdd.app.android.interestcollection.ui.images.model.Image;
import org.wdd.app.android.interestcollection.ui.jokes.activity.DirtyJokeDetailActivity;
import org.wdd.app.android.interestcollection.ui.jokes.model.DirtyJoke;
import org.wdd.app.android.interestcollection.ui.shares.activity.ShareDetailActivity;
import org.wdd.app.android.interestcollection.ui.shares.model.Share;
import org.wdd.app.android.interestcollection.ui.videos.activity.VideoDetailActivity;
import org.wdd.app.android.interestcollection.ui.videos.model.Video;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by wangdd on 17-6-2.
 */

public class MediaChooserActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String action = intent.getAction();
        if (!Intent.ACTION_VIEW.equals(action)) {
            finish();
            return;
        }
        Uri uri = intent.getData();
        if (uri == null) {
            finish();
            return;
        }
        String typeStr = uri.getQueryParameter("type");
        if (TextUtils.isEmpty(typeStr)) {
            finish();
            return;
        }
        String url = uri.getQueryParameter("path");
        if (TextUtils.isEmpty(url)) {
            finish();
            return;
        }
        try {
            url = URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            finish();
            return;
        }
        String imgUrl = uri.getQueryParameter("ico");
        if (!TextUtils.isEmpty(imgUrl)) {
            try {
                imgUrl = URLDecoder.decode(imgUrl, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        String title = uri.getQueryParameter("name");
        String date = uri.getQueryParameter("date");
        if(typeStr.equals("1")) {
            DirtyJoke joke = new DirtyJoke();
            joke.url = url;
            joke.imgUrl = imgUrl;
            joke.title = title;
            joke.date = date;
            DirtyJokeDetailActivity.show(this, joke);
        } else if(typeStr.equals("2")) {
            Image image = new Image();
            image.url = url;
            image.imgUrl = imgUrl;
            image.title = title;
            image.date = date;
            boolean isGif = false;
            String gifStr = uri.getQueryParameter("gif");
            if (!TextUtils.isEmpty(gifStr)) {
                isGif = gifStr.equals("1") ? true : false;
            }
            image.isGif = isGif;
            ImageDetailActivity.show(this, image);
        } else if(typeStr.equals("3")) {
            Video video = new Video();
            video.url = url;
            video.imgUrl = imgUrl;
            video.title = title;
            video.date = date;
            VideoDetailActivity.show(this, video);
        } else if(typeStr.equals("4")) {
            Audio audio = new Audio();
            audio.url = url;
            audio.imgUrl = imgUrl;
            audio.title = title;
            audio.date = date;
            AudioDetailActivity.show(this, audio);
        } else if(typeStr.equals("5")) {
            Share share = new Share();
            share.url = url;
            share.imgUrl = imgUrl;
            share.title = title;
            share.date = date;
            ShareDetailActivity.show(this, share);
        }
        finish();
    }
}
