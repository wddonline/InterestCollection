package org.wdd.app.android.interestcollection.ui.images.model;

import org.wdd.app.android.interestcollection.ui.jokes.model.DirtyJokeDetail;

import java.util.List;

/**
 * Created by richard on 2/24/17.
 */

public class ImageDetail {

    public String title;
    public String time;
    public String tag;
    public String commentCount;
    public String imgUrl;
    public List<DirtyJokeDetail.Post> posts;
    public String source;

    public static class Post {

        public String content;
        public List<DirtyJokeDetail.Comment> comments;

    }

    public static class Comment {

        public String type;
        public String author;
        public String comment;

    }

}
