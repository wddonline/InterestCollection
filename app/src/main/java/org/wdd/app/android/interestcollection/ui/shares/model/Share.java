package org.wdd.app.android.interestcollection.ui.shares.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by richard on 2/24/17.
 */

public class Share implements Parcelable {

    public String url;
    public String imgUrl;
    public String title;
    public String date;

    public Share() {
    }

    protected Share(Parcel in) {
        url = in.readString();
        imgUrl = in.readString();
        title = in.readString();
        date = in.readString();
    }

    public static final Creator<Share> CREATOR = new Creator<Share>() {
        @Override
        public Share createFromParcel(Parcel in) {
            return new Share(in);
        }

        @Override
        public Share[] newArray(int size) {
            return new Share[size];
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
