package org.wdd.app.android.interestcollection.ui.jokes.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by richard on 2/23/17.
 */

public class DirtyJoke implements Parcelable {

    public String url;
    public String imgUrl;
    public String title;
    public String date;

    public DirtyJoke() {
    }

    protected DirtyJoke(Parcel in) {
        url = in.readString();
        imgUrl = in.readString();
        title = in.readString();
        date = in.readString();
    }

    public static final Creator<DirtyJoke> CREATOR = new Creator<DirtyJoke>() {
        @Override
        public DirtyJoke createFromParcel(Parcel in) {
            return new DirtyJoke(in);
        }

        @Override
        public DirtyJoke[] newArray(int size) {
            return new DirtyJoke[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(imgUrl);
        dest.writeString(title);
        dest.writeString(date);
    }
}
