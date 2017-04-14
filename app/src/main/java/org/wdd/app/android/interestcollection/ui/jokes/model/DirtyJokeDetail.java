package org.wdd.app.android.interestcollection.ui.jokes.model;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wdd.app.android.interestcollection.ui.main.model.HtmlHref;

import java.util.List;
import java.util.Map;

/**
 * Created by richard on 2/24/17.
 */

public class DirtyJokeDetail {

    public enum PostType {
        HEADER,
        TEXT,
        IMAGE
    }

    public String title;
    public String time;
    public String tag;
    public String commentCount;
    public String source;
    public List<Post> post;

    public static class Post {

        public PostType type;
        public String content;

    }

}
