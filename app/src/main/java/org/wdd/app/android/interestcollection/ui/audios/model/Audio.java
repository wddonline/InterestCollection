package org.wdd.app.android.interestcollection.ui.audios.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by richard on 2/24/17.
 */

public class Audio implements Parcelable {

    public String url;
    public String imgUrl;
    public String title;
    public String date;

    public Audio() {
    }

    protected Audio(Parcel in) {
        url = in.readString();
        imgUrl = in.readString();
        title = in.readString();
        date = in.readString();
    }

    public static final Creator<Audio> CREATOR = new Creator<Audio>() {
        @Override
        public Audio createFromParcel(Parcel in) {
            return new Audio(in);
        }

        @Override
        public Audio[] newArray(int size) {
            return new Audio[size];
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
