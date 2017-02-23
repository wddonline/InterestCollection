package org.wdd.app.android.interestcollection.database.model;

/**
 * Created by richard on 1/22/17.
 */

public class DbPhotoFavorite {

    public int id;
    public String name;
    public String url;
    public String imgUrl;

    public DbPhotoFavorite() {
    }

    public DbPhotoFavorite(String name, String url, String imgUrl) {
        this.name = name;
        this.url = url;
        this.imgUrl = imgUrl;
    }

}
