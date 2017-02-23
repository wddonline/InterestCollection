package org.wdd.app.android.interestcollection.ui.main.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by richard on 2/10/17.
 */

public class HtmlHref implements Parcelable {

    public String name;
    public String url;
    public String imgUrl;

    public HtmlHref() {

    }

    public HtmlHref(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public HtmlHref(String name, String url, String imgUrl) {
        this.name = name;
        this.url = url;
        this.imgUrl = imgUrl;
    }

    protected HtmlHref(Parcel in) {
        name = in.readString();
        url = in.readString();
        imgUrl = in.readString();
    }

    public static final Creator<HtmlHref> CREATOR = new Creator<HtmlHref>() {
        @Override
        public HtmlHref createFromParcel(Parcel in) {
            return new HtmlHref(in);
        }

        @Override
        public HtmlHref[] newArray(int size) {
            return new HtmlHref[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(url);
        dest.writeString(imgUrl);
    }
}
