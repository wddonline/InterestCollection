package org.wdd.app.android.interestcollection.ui.jokes.model;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

/**
 * Created by richard on 2/24/17.
 */

public class DirtyJokeDetail {

    public String title;
    public String time;
    public String tag;
    public String commentCount;
    public String imgUrl;
    public List<Post> posts;
    public String source;

    public static class Post {

        public String content;
        public List<Comment> comments;

    }

    public static class Comment {

        public String type;
        public String author;
        public String comment;

    }
}
