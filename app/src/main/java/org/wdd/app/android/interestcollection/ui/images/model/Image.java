package org.wdd.app.android.interestcollection.ui.images.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by richard on 2/23/17.
 */

public class Image implements Parcelable {

    public String url;
    public String title;
    public String imgUrl;
    public String imgCount;
    public boolean isGif;
    public String date;

    public Image() {
    }

    protected Image(Parcel in) {
        url = in.readString();
        title = in.readString();
        imgUrl = in.readString();
        imgCount = in.readString();
        isGif = in.readByte() != 0;
        date = in.readString();
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(title);
        dest.writeString(imgUrl);
        dest.writeString(imgCount);
        dest.writeByte((byte) (isGif ? 1 : 0));
        dest.writeString(date);
    }
}
